package donatr

sealed trait Event

case class DonaterCreated(donater: Donater) extends Event
case class DonatableCreated(donatable: Donatable) extends Event
case class FundableCreated(fundable: Fundable) extends Event
case class DonationCreated(donation: Donation) extends Event

case class EventOrFailure(event: Option[Event] = None, failureMessage: Option[Exception] = None)
