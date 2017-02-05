package donatr

import java.util.UUID

class DonatrCore(val eventStore: EventStore = new EventStore()) {

  import scala.concurrent.ExecutionContext
  import ExecutionContext.Implicits.global

  var state = DonatrState()

  rebuildState()

  def rebuildState(): Unit = {
    resetState()
    eventStore.getEvents.foreach { e =>
      state = state.apply(e)
    }
  }

  def resetState(): Unit = {
    state = DonatrState()
  }

  def processCommand(create: CreateDonater): Either[Throwable, DonaterCreated] = {
    val d = create.donater
    Either.cond(state.donaters.count(_._2.name == d.name) == 0,
      DonaterCreated(Donater(UUID.randomUUID(), d.name, d.email, d.balance)),
      NameTaken())
      .flatMap(persistEvent)
  }

  def processCommand(create: CreateDonatable): Either[Throwable, DonatableCreated] = {
    val d = create.donatable
    Either.cond(state.donatables.count(_._2.name == d.name) == 0,
      DonatableCreated(Donatable(UUID.randomUUID(), d.name, d.minDonationAmount, d.balance)),
      NameTaken())
      .flatMap(persistEvent)
  }

  def processCommand(create: CreateFundable): Either[Throwable, FundableCreated] = {
    val d = create.fundable
    Either.cond(state.fundables.count(_._2.name == d.name) == 0,
      FundableCreated(Fundable(UUID.randomUUID(), d.name, d.fundingTarget, d.balance)),
      NameTaken())
      .flatMap(persistEvent)
  }

  def processCommand(create: Withdraw): Either[Throwable, Withdrawn] = {
    (state.donaters.contains(create.entityId),
      state.donatables.contains(create.entityId),
      state.fundables.contains(create.entityId)
    ) match {
      case (true, false, false) =>
        persistEvent(Withdrawn(create.donationId, create.entityId, create.withdrawValue))
      case (false, true, false) =>
        persistEvent(Withdrawn(create.donationId, create.entityId, create.withdrawValue))
      case (false, false, true) =>
        persistEvent(Withdrawn(create.donationId, create.entityId, create.withdrawValue))
      case _ => Left(UnknownEntity(create.entityId))
    }
  }

  def processCommand(create: Deposit): Either[Throwable, Deposited] = {
    (state.donaters.contains(create.entityId),
      state.donatables.contains(create.entityId),
      state.fundables.contains(create.entityId)
    ) match {
      case (true, false, false) =>
        persistEvent(Deposited(create.donationId, create.entityId, create.depositValue))
      case (false, true, false) =>
        persistEvent(Deposited(create.donationId, create.entityId, create.depositValue))
      case (false, false, true) =>
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

  private def persistEvent[E <: Event](event: E): Either[Throwable, E] = {
    eventStore.insert(event) match {
      case Right(_) =>
        state = state.apply(event)
        Right(event)
      case Left(err) => Left(err)
    }
  }
}
