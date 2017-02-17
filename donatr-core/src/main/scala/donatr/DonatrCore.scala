package donatr

import java.util.UUID

import org.slf4j.LoggerFactory

import scala.math.BigDecimal.RoundingMode


class DonatrCore(implicit
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

  def rebuildState(): Unit = {
    log.info("rebuilding state started")
    resetState()

    if (eventStore.getEvents.isEmpty) {
      eventStore.insert(LedgerCreated(state.ledger))
    }

    eventStore.getEvents.foreach { e =>
      state = state.apply(e)
    }

    log.info("rebuilt state")
  }

  def resetState(): Unit = {
    state = DonatrState(ledger = ledger.copy(balance = 0))
  }

  implicit val CreateDonaterC: CreateCommand[DonaterWithoutId] = CreateCommand.instance(d => createDonater(d))
  implicit val CreateDonatableC: CreateCommand[DonatableWithoutId] = CreateCommand.instance(d => createDonatable(d))
  implicit val CreateFundableC: CreateCommand[FundableWithoutId] = CreateCommand.instance(d => createFundable(d))

  implicit val ChangeDonaterNameC: ChangeNameCommand[Donater] = ChangeNameCommand.instance((donater, name) =>
    changeDonaterName(donater, name))

  def create[T](entity: T)(implicit creator: CreateCommand[T]): Either[Throwable, UUID] = {
    creator.create(entity)
  }

  def changeName[T](obj: T, name: Name)(implicit creator: ChangeNameCommand[T]): Either[Throwable, UUID] = {
    creator.changeName(obj, name)
  }

  private def persistEvent[E <: Event](event: E): Either[Throwable, E] = {
    eventStore.insert(event) match {
      case Right(_) =>
        state = state.apply(event)
        eventPublisher.publish(event)
        Right(event)
      case Left(err) => Left(err)
    }
  }

  private def persistEvent2[E <: Event](event: E): Either[Throwable, E] = {
    eventStore.insert(event) match {
      case Right(_) => Right(event)
      case Left(err) => Left(err)
    }
  }

  private def processEvent[E <: Event](eitherEvent: Either[Throwable, E], f: E => Unit) = {
    eitherEvent.flatMap(persistEvent2)
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

  def processCommand(donater: CreateDonater): Either[Throwable, DonaterCreated] = {
    val d = donater.donater
    Either.cond(d.name.nonEmpty && state.donaters.count(_._2.name == d.name) == 0,
      DonaterCreated(Donater(UUID.randomUUID(), d.name, d.email, d.balance)),
      NameTaken())
      .flatMap(persistEvent)
  }

  def processCommand(donatable: CreateDonatable): Either[Throwable, DonatableCreated] = {
    val d = donatable.donatable
    Either.cond(d.name.nonEmpty && state.donatables.count(_._2.name == d.name) == 0,
      DonatableCreated(Donatable(UUID.randomUUID(), d.name, d.imageUrl, d.minDonationAmount, d.balance)),
      NameTaken())
      .flatMap(persistEvent)
  }

  def processCommand(fundable: CreateFundable): Either[Throwable, FundableCreated] = {
    val d = fundable.fundable
    Either.cond(d.name.nonEmpty && state.fundables.count(_._2.name == d.name) == 0,
      FundableCreated(Fundable(UUID.randomUUID(), d.name, d.imageUrl, d.fundingTarget, d.balance)),
      NameTaken())
      .flatMap(persistEvent)
  }

  def processCommand(withdraw: Withdraw): Either[Throwable, Withdrawn] = {
    if (state.donaters.contains(withdraw.entityId)) {
      val throwableOrWithdrawn = persistEvent(Withdrawn(withdraw.donationId, withdraw.entityId, withdraw.withdrawValue))
      eventPublisher.publish(DonaterUpdated(state.donaters(withdraw.entityId)))
      throwableOrWithdrawn
    } else {
      persistEvent(Withdrawn(withdraw.donationId, state.ledger.id, withdraw.withdrawValue))
    }
  }

  def processCommand(deposit: Deposit): Either[Throwable, Deposited] = {
    (state.donaters.get(deposit.entityId),
      state.donatables.get(deposit.entityId),
      state.fundables.get(deposit.entityId)
    ) match {
      case (Some(donater), None, None) =>
        val throwableOrDeposited = persistEvent(Deposited(deposit.donationId, deposit.entityId, deposit.depositValue))
        eventPublisher.publish(DonaterUpdated(state.donaters(donater.id)))
        throwableOrDeposited
      case (None, Some(donatable), None) =>
        val depositValue = deposit.depositValue.setScale(2, RoundingMode.UP)
        val minDonationAmount = donatable.minDonationAmount.setScale(2, RoundingMode.UP)
        Either.cond((depositValue - minDonationAmount) > -0.1,
          Deposited(deposit.donationId, deposit.entityId, deposit.depositValue),
          BelowMinDonationAmount(donatable.minDonationAmount, deposit.depositValue)
        ).flatMap(persistEvent)
      case (None, None, Some(fundable)) =>
        val throwableOrDeposited = persistEvent(Deposited(deposit.donationId, deposit.entityId, deposit.depositValue))
        eventPublisher.publish(FundableUpdated(state.fundables(fundable.id)))
        throwableOrDeposited
      case _ => Left(UnknownEntity(deposit.entityId))
    }
  }

  def processCommand(donation: CreateDonation): Either[Throwable, DonationCreated] = {
    val d = donation.donation
    val newId = UUID.randomUUID()
    processCommand(Withdraw(newId, d.from, d.value))
      .flatMap(f => processCommand(Deposit(f.donationId, d.to, d.value)))
      .flatMap(f => persistEvent(DonationCreated(Donation(f.donationId, d.from, d.to, d.value))))
      .fold(err => Left(err), event => Right(event))
  }

  def processCommand(ledgerDonation: CreateLedgerDonation): Either[Throwable, DonationCreated] = {
    processCommand(
      CreateDonation(DonationWithoutId(state.ledger.id, ledgerDonation.donation.to, ledgerDonation.donation.value)))
  }

  def processCommand(change: ChangeDonaterName): Either[Throwable, DonaterNameChanged] = {
    val nameAvailable = change.name.nonEmpty && state.donaters.count(_._2.name == change.name) == 0
    val throwableOrNameChanged = Either.cond(nameAvailable,
      DonaterNameChanged(change.donaterId, change.name),
      NameTaken())
      .flatMap(persistEvent)
    eventPublisher.publish(DonaterUpdated(state.donaters(change.donaterId)))
    throwableOrNameChanged
  }
}
