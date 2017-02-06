package donatr

import java.util.UUID

class DonatrCore(val eventStore: EventStore = new EventStore(),
                 ledger: Ledger)(implicit val eventPublisher: EventPublisher)
{

  import scala.concurrent.ExecutionContext
  import ExecutionContext.Implicits.global

  var state = DonatrState(ledger = ledger)

  rebuildState()

  def rebuildState(): Unit = {
    resetState()

    if(eventStore.getEvents.isEmpty) {
      eventStore.insert(LedgerCreated(state.ledger))
    }

    eventStore.getEvents.foreach { e =>
      state = state.apply(e)
    }
  }

  def resetState(): Unit = {
    state = DonatrState(ledger = ledger.copy(balance = 0))
  }

  def processCommand(create: CreateDonater): Either[Throwable, DonaterCreated] = {
    val d = create.donater
    Either.cond(d.name.nonEmpty && state.donaters.count(_._2.name == d.name) == 0,
      DonaterCreated(Donater(UUID.randomUUID(), d.name, d.email, d.balance)),
      NameTaken())
      .flatMap(persistEvent)
  }

  def processCommand(create: CreateDonatable): Either[Throwable, DonatableCreated] = {
    val d = create.donatable
    Either.cond(d.name.nonEmpty && state.donatables.count(_._2.name == d.name) == 0,
      DonatableCreated(Donatable(UUID.randomUUID(), d.name, d.minDonationAmount, d.balance)),
      NameTaken())
      .flatMap(persistEvent)
  }

  def processCommand(create: CreateFundable): Either[Throwable, FundableCreated] = {
    val d = create.fundable
    Either.cond(d.name.nonEmpty && state.fundables.count(_._2.name == d.name) == 0,
      FundableCreated(Fundable(UUID.randomUUID(), d.name, d.fundingTarget, d.balance)),
      NameTaken())
      .flatMap(persistEvent)
  }

  def processCommand(create: Withdraw): Either[Throwable, Withdrawn] = {
    if (state.donaters.contains(create.entityId)) {
      persistEvent(Withdrawn(create.donationId, create.entityId, create.withdrawValue))
    } else {
      persistEvent(Withdrawn(create.donationId, state.ledger.id, create.withdrawValue))
    }
  }

  def processCommand(create: Deposit): Either[Throwable, Deposited] = {
    (state.donaters.get(create.entityId),
      state.donatables.get(create.entityId),
      state.fundables.get(create.entityId)
    ) match {
      case (Some(_), None, None) =>
        persistEvent(Deposited(create.donationId, create.entityId, create.depositValue))
      case (None, Some(donatable), None) =>
        Either.cond(create.depositValue >= donatable.minDonationAmount,
          Deposited(create.donationId, create.entityId, create.depositValue),
          BelowMinDonationAmount(donatable.minDonationAmount, create.depositValue)
        ).flatMap(persistEvent)
      case (None, None, Some(_)) =>
        persistEvent(Deposited(create.donationId, create.entityId, create.depositValue))
      case _ => Left(UnknownEntity(create.entityId))
    }
  }

  def processCommand(create: CreateDonation): Either[Throwable, DonationCreated] = {
    val d = create.donation
    val newId = UUID.randomUUID()
    processCommand(Withdraw(newId, d.from, d.value))
      .flatMap(f => processCommand(Deposit(f.donationId, d.to, d.value)))
      .flatMap(f => persistEvent(DonationCreated(Donation(f.donationId, d.from, d.to, d.value))))
      .fold(err => Left(err), event => Right(event))
  }

  def processCommand(create: CreateLedgerDonation): Either[Throwable, DonationCreated] = {
    processCommand(CreateDonation(DonationWithoutId(state.ledger.id, create.donation.to, create.donation.value)))
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
