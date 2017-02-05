package donatr

import java.util.UUID

class DonatrCore(val eventStore: EventStore = new EventStore()) {

  import scala.concurrent.ExecutionContext
  import ExecutionContext.Implicits.global

  var state = DonatrState()

  rebuildState()

  private def rebuildState(): Unit = {
    eventStore.getEvents.foreach { e =>
      state = state.apply(e)
    }
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

  def processCommand(create: CreateDonation): Either[Throwable, DonationCreated] = {
    val d = create.donation
    val newId = UUID.randomUUID()
    persistEvent(Withdrawn(newId, d.from, d.value))
    persistEvent(Deposited(newId, d.to, d.value))
    persistEvent(DonationCreated(Donation(newId, d.from, d.to, d.value))) match {
      case Right(event) => Right(event)
      case Left(err) => Left(err)
    }
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
