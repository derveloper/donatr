package donatr

import donatr.DonatrTypes._

sealed trait Event

case class LedgerCreated(ledger: Ledger) extends Event

case class DonaterCreated(donater: Donater) extends Event
case class DonaterUpdated(donater: Donater) extends Event
case class DonaterNameChanged(donaterId: Id, name: Name) extends Event

case class DonatableCreated(donatable: Donatable) extends Event

case class FundableCreated(fundable: Fundable) extends Event

case class FundableUpdated(fundable: Fundable) extends Event

case class DonationCreated(donation: Donation) extends Event

case class Withdraw(donationId: Id, entityId: Id, withdrawValue: Value) extends Event
case class Deposit(donationId: Id, entityId: Id, depositValue: Value) extends Event

case class Withdrawn(donationId: Id, entityId: Id, withdrawValue: Value) extends Event

case class Deposited(donationId: Id, entityId: Id, depositValue: Value) extends Event
