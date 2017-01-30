package donatr

import java.util.UUID

import scala.concurrent.ExecutionContext

object DonatrCore {
  val eventStore = new EventStore()
  var state = DonatrState()

  def rebuildState(implicit ec: ExecutionContext): Unit = {
    eventStore.getEvents.foreach { e =>
      state = state.apply(e)
    }
  }

  def processCommand(command: Command): EventOrFailure = {
    val result = command match {
      case CreateDonatable(Donatable(_, newName, balance)) => handleCreate(newName, balance)
      case _ => EventOrFailure(None, Some(UnknownCommand(command)))
    }
    result match {
      case EventOrFailure(Some(event), None) =>
        eventStore.insert(event)
        result
      case _ => result
    }
  }

  private def handleCreate(name: String, balance: BigDecimal) = {
    state.donatables.count(d => d.name == name) match {
      case 0 =>
        val newId = UUID.randomUUID()
        val created = DonatableCreated(Donatable(Some(newId), name, balance))
        state = state.apply(created)
        EventOrFailure(Some(created))
      case _ => EventOrFailure(None, Some(DonatableNameTaken()))
    }
  }
}
