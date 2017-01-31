package donatr

import java.util.UUID

object DonatrCore {
  import scala.concurrent.ExecutionContext

  val eventStore = new EventStore()
  var state = DonatrState()

  def rebuildState()(implicit ec: ExecutionContext): Unit = {
    eventStore.getEvents.foreach { e =>
      state = state.apply(e)
    }
  }

  def processCommand(create: CreateDonater): EventOrFailure = {
    val d = create.donater
    if (state.donaters.count(_._2.name == d.name) == 0) {
      val newId = UUID.randomUUID()
      val created = DonaterCreated(Donater(newId, d.name, d.email, d.balance))
      persistEvent(created)
    } else {
      EventOrFailure(None, Some(NameTaken()))
    }
  }

  def processCommand(create: CreateDonatable): EventOrFailure = {
    val d = create.donatable
    if (state.donatables.count(_._2.name == d.name) == 0) {
      val newId = UUID.randomUUID()
      val created = DonatableCreated(Donatable(newId, d.name, d.minDonationAmount, d.balance))
      persistEvent(created)
    } else {
      EventOrFailure(None, Some(NameTaken()))
    }
  }

  def processCommand(create: CreateFundable): EventOrFailure = {
    val d = create.fundable
    if (state.fundables.count(_._2.name == d.name) == 0) {
      val newId = UUID.randomUUID()
      val created = FundableCreated(Fundable(newId, d.name, d.fundingTarget, d.balance))
      persistEvent(created)
    } else {
      EventOrFailure(None, Some(NameTaken()))
    }
  }

  def processCommand(create: CreateDonation): EventOrFailure = {
    val d = create.donation
    val newId = UUID.randomUUID()
    val created = DonationCreated(Donation(newId, d.from, d.to, d.value))
    persistEvent(created)
  }

  private def persistEvent(event: Event) = {
    eventStore.insert(event)
    state = state.apply(event)
    EventOrFailure(Some(event))
  }
}
