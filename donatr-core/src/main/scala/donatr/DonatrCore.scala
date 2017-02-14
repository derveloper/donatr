package donatr

import java.util.UUID

import org.slf4j.LoggerFactory

import scala.math.BigDecimal.RoundingMode

class DonatrCore(val eventStore: EventStore = new EventStore(),
                 initialLedger: Ledger)(implicit val eventPublisher: EventPublisher) {
  private val log = LoggerFactory.getLogger(this.getClass)

  import scala.concurrent.ExecutionContext
  import ExecutionContext.Implicits.global

  private var state = DonatrState(ledger = initialLedger)

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

  private def persistEvent[E <: Event](event: E): Either[Throwable, E] = {
    eventStore.insert(event) match {
      case Right(_) =>
        state = state.apply(event)
        eventPublisher.publish(event)
        Right(event)
      case Left(err) => Left(err)
    }
  }
}
