package donatr

import java.util.UUID

object DonatrCore {

  import scala.concurrent.ExecutionContext
  import ExecutionContext.Implicits.global

  val eventStore = new EventStore()
  var state = DonatrState()

  rebuildState()

  private def rebuildState(): Unit = {
    eventStore.getEvents.foreach { e =>
      state = state.apply(e)
    }
  }

  def processCommand(create: CreateDonater): Either[NameTaken, DonaterCreated] = {
    val d = create.donater
      Either.cond(state.donaters.count(_._2.name == d.name) == 0,
        persistEvent(DonaterCreated(Donater(UUID.randomUUID(), d.name, d.email, d.balance))),
        NameTaken())
  }

  def processCommand(create: CreateDonatable): Either[NameTaken, DonatableCreated] = {
    val d = create.donatable
    Either.cond(state.donatables.count(_._2.name == d.name) == 0,
      persistEvent(DonatableCreated(Donatable(UUID.randomUUID(), d.name, d.minDonationAmount, d.balance))),
      NameTaken())
  }

  def processCommand(create: CreateFundable): Either[NameTaken, FundableCreated] = {
    val d = create.fundable
    Either.cond(state.fundables.count(_._2.name == d.name) == 0,
      persistEvent(FundableCreated(Fundable(UUID.randomUUID(), d.name, d.fundingTarget, d.balance))),
      NameTaken())
  }

  def processCommand(create: CreateDonation): DonationCreated = {
    val d = create.donation
    val newId = UUID.randomUUID()
    persistEvent(Withdrawn(newId, d.from, d.value))
    persistEvent(Deposited(newId, d.to, d.value))
    persistEvent(DonationCreated(Donation(newId, d.from, d.to, d.value)))
  }

  private def persistEvent[E <: Event](event: E) = {
    eventStore.insert(event)
    state = state.apply(event)
    event
  }
}
