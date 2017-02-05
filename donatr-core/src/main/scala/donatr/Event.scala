package donatr

import java.util.UUID

sealed trait Event

case class LedgerCreated(ledger: Ledger) extends Event

case class DonaterCreated(donater: Donater) extends Event

case class DonatableCreated(donatable: Donatable) extends Event

case class FundableCreated(fundable: Fundable) extends Event

case class DonationCreated(donation: Donation) extends Event

case class Withdraw(donationId: UUID, entityId: UUID, withdrawValue: BigDecimal) extends Event
case class Deposit(donationId: UUID, entityId: UUID, depositValue: BigDecimal) extends Event

case class Withdrawn(donationId: UUID, entityId: UUID, withdrawValue: BigDecimal) extends Event

case class Deposited(donationId: UUID, entityId: UUID, depositValue: BigDecimal) extends Event

case class EventOrFailure(event: Option[Event] = None, failureMessage: Option[Exception] = None)
