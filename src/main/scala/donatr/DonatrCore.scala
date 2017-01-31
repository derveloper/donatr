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

  def processCommand(command: Command): EventOrFailure = command match {
    case CreateDonater(DonaterWithoutId(newName, email, balance)) =>
      handleCreate(DonaterWithoutId(newName, email, balance))
    case CreateDonatable(DonatableWithoutId(newName, value, balance)) =>
      handleCreate(DonatableWithoutId(newName, value, balance))
    case _ => EventOrFailure(None, Some(UnknownCommand(command)))
  }

  private def handleCreate(donater: DonaterWithoutId) = donater match {
    case DonaterWithoutId(name, email, balance) =>
      if (state.donaters.count(d => d._2.name == name) == 0) {
        val newId = UUID.randomUUID()
        val created = DonaterCreated(Donater(newId, name, email, balance))
        persistEvent(created)
      } else {
        EventOrFailure(None, Some(NameTaken()))
      }
    case _ => EventOrFailure(None, Some(UnknownEntity(donater)))
  }

  private def handleCreate(donatable: DonatableWithoutId) = donatable match {
    case DonatableWithoutId(name, minDonationAmount, balance) =>
      if (state.donatables.count(d => d._2.name == name) == 0) {
        val newId = UUID.randomUUID()
        val created = DonatableCreated(Donatable(newId, name, minDonationAmount, balance))
        persistEvent(created)
      } else {
        EventOrFailure(None, Some(NameTaken()))
      }
    case _ => EventOrFailure(None, Some(UnknownEntity(donatable)))
  }

  private def persistEvent(event: Event) = {
    eventStore.insert(event)
    state = state.apply(event)
    EventOrFailure(Some(event))
  }
}
