package donatr

sealed trait Event

case class DonatableCreated(donatable: Donatable) extends Event
case class FixedValueDonatableCreated(donatable: FixedValueDonatable) extends Event

case class EventOrFailure(event: Option[Event] = None, failureMessage: Option[Exception] = None)
