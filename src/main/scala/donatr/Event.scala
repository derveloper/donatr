package donatr

sealed trait Event

case class DonaterCreated(donatable: Donater) extends Event
case class DonatableCreated(donatable: Donatable) extends Event
case class FundableCreated(donatable: Fundable) extends Event

case class EventOrFailure(event: Option[Event] = None, failureMessage: Option[Exception] = None)
