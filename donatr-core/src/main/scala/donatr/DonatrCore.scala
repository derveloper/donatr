package donatr

import java.util.UUID

import org.slf4j.LoggerFactory

import scala.math.BigDecimal.RoundingMode


trait EventProcessor {
  def persistEvent[E <: Event](event: E): Either[Throwable, E]

  def processEvent[E <: Event](eitherEvent: Either[Throwable, E], f: E => Unit): Either[Throwable, E]
}

class DonatrCore(implicit
                 val eventProcessor: EventProcessor,
                 val eventStore: EventStore,
                 val eventPublisher: EventPublisher) {
  private val log = LoggerFactory.getLogger(this.getClass)

  import DonatrTypes._

  import scala.concurrent.ExecutionContext
  import ExecutionContext.Implicits.global

  implicit var state = DonatrState(ledger = Ledger(UUID.randomUUID()))

  import DonatrState._

  rebuildState()

  def donaters: Map[UUID, Donater] = state.donaters

  def donatables: Map[UUID, Donatable] = state.donatables

  def fundables: Map[UUID, Fundable] = state.fundables

  def donations: Map[UUID, Donation] = state.donations

  def ledger: Ledger = state.ledger

  def events: Seq[Event] = eventStore.getEvents

  def rebuildState(): Unit = {
    log.info("rebuilding state started")
    resetState()

    if (events.isEmpty) {
      eventStore.insert(LedgerCreated(state.ledger))
    }

    events.foreach { e =>
      state = state.reduce(state, e)
    }

    log.info("rebuilt state")
  }

  def resetState(): Unit = {
    state = DonatrState(ledger = ledger.copy(balance = 0))
  }

  implicit val CreateDonaterC: CreateCommand[DonaterWithoutId] = CreateCommand.instance(d => createDonater(d))
  implicit val CreateDonatableC: CreateCommand[DonatableWithoutId] = CreateCommand.instance(d => createDonatable(d))
  implicit val CreateFundableC: CreateCommand[FundableWithoutId] = CreateCommand.instance(d => createFundable(d))
  implicit val CreateDonationC: CreateCommand[DonationWithoutId] = CreateCommand.instance(d => createDonation(d))
  implicit val CreateDonationWithoutFromC: CreateCommand[DonationWithoutIdAndFrom] =
    CreateCommand.instance(d => createDonation(d))

  implicit val ChangeDonaterNameC: ChangeNameCommand[Donater] = ChangeNameCommand.instance((donater, name) =>
    changeDonaterName(donater, name))

  def create[T](entity: T)(implicit creator: CreateCommand[T]): Either[Throwable, UUID] = {
    creator.create(entity)
  }

  def changeName[T](obj: T, name: Name)(implicit creator: ChangeNameCommand[T]): Either[Throwable, UUID] = {
    creator.changeName(obj, name)
  }

  def persistEvent[E <: Event](event: E): Either[Throwable, E] = {
    eventStore.insert(event) match {
      case Right(_) => Right(event)
      case Left(err) => Left(err)
    }
  }

  def processEvent[E <: Event](eitherEvent: Either[Throwable, E], f: E => Unit): Either[Throwable, E] = {
    eitherEvent.flatMap(persistEvent)
      .flatMap(e => {
        f(e)
        Right(e)
      })
      .flatMap(e => {
        eventPublisher.publish(e)
        Right(e)
      })
  }

  private def createDonater(donater: DonaterWithoutId) = {
    processEvent(Either.cond(donater.name.nonEmpty && state.donaters.count(_._2.name == donater.name) == 0,
      DonaterCreated(Donater(UUID.randomUUID(), donater.name, donater.email, donater.balance)),
      NameTaken()), (e: DonaterCreated) => state = state.reduce(state, e))
      .map(_.donater.id)
  }

  private def createDonatable(donatable: DonatableWithoutId) = {
    processEvent(Either.cond(donatable.name.nonEmpty && state.donatables.count(_._2.name == donatable.name) == 0,
      DonatableCreated(Donatable(
          UUID.randomUUID(), donatable.name, donatable.imageUrl, donatable.minDonationAmount, donatable.balance)),
      NameTaken()), (e: DonatableCreated) => state = state.reduce(state, e))
      .map(_.donatable.id)
  }

  private def createFundable(fundable: FundableWithoutId) = {
    processEvent(Either.cond(fundable.name.nonEmpty && state.fundables.count(_._2.name == fundable.name) == 0,
      FundableCreated(Fundable(
        UUID.randomUUID(), fundable.name, fundable.imageUrl, fundable.fundingTarget, fundable.balance)),
      NameTaken()), (e: FundableCreated) => state = state.reduce(state, e))
      .map(_.fundable.id)
  }

  private def changeDonaterName(donater: Donater, name: String): Either[Throwable, UUID] = {
    val id = donater.id
    val nameAvailable = name.nonEmpty && state.donaters.count(_._2.name == name) == 0
    processEvent(Either.cond(nameAvailable,
      DonaterNameChanged(id, name),
      NameTaken()), (e: DonaterNameChanged) => state = state.reduce(state, e))
      .map(_.donaterId)
      .flatMap(id => {
        eventPublisher.publish(DonaterUpdated(state.donaters(id)))
        Right(id)
      })
  }

  def createDonation(donation: DonationWithoutId): Either[Throwable, UUID] = {
    val d = donation
    val newId = UUID.randomUUID()

    withdraw(newId, d.from, d.value)
      .flatMap(_ => deposit(newId, d.to, d.value))
      .flatMap(f => persistEvent(DonationCreated(Donation(f.donationId, d.from, d.to, d.value))))
      .fold(err => Left(err), event => Right(event.donation.id))
  }

  private def createDonation(donation: DonationWithoutIdAndFrom): Either[Throwable, UUID] = {
    createDonation(DonationWithoutId(state.ledger.id, donation.to, donation.value))
  }

  private def withdraw(donationId: Id, fromId: Id, value: Value) = {
    if (state.donaters.contains(fromId)) {
      val throwableOrWithdrawn =
        processEvent(Right(Withdrawn(donationId, fromId, value)), (e: Withdrawn) => state = state.reduce(state, e))
      eventPublisher.publish(DonaterUpdated(state.donaters(fromId)))
      throwableOrWithdrawn
    } else {
      processEvent(Right(
        Withdrawn(donationId, state.ledger.id, value)), (e: Withdrawn) => state = state.reduce(state, e))
    }
  }

  private def deposit(donationId: Id, toId: Id, value: Value) = {
    (state.donaters.get(toId),
      state.donatables.get(toId),
      state.fundables.get(toId)
    ) match {
      case (Some(donater), None, None) =>
        val throwableOrDeposited =
          processEvent(Right(Deposited(donationId, toId, value)), (e: Deposited) => state = state.reduce(state, e))
        eventPublisher.publish(DonaterUpdated(state.donaters(donater.id)))
        throwableOrDeposited
      case (None, Some(donatable), None) =>
        val depositValue = value.setScale(2, RoundingMode.UP)
        val minDonationAmount = donatable.minDonationAmount.setScale(2, RoundingMode.UP)
        Either.cond((depositValue - minDonationAmount) > -0.1,
          Deposited(donationId, toId, value),
          BelowMinDonationAmount(donatable.minDonationAmount, value))
          .flatMap(e => processEvent(Right(e), (e: Deposited) => state = state.reduce(state, e)))
      case (None, None, Some(fundable)) =>
        val throwableOrDeposited =
          processEvent(Right(Deposited(donationId, toId, value)), (e: Deposited) => state = state.reduce(state, e))
        eventPublisher.publish(FundableUpdated(state.fundables(fundable.id)))
        throwableOrDeposited
      case _ => Left(UnknownEntity(toId))
    }
  }
}
