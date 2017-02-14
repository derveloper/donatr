package donatr

import java.util.UUID

sealed trait Command

case class CreateDonater(donater: DonaterWithoutId) extends Command

case class CreateDonatable(donatable: DonatableWithoutId) extends Command

case class CreateFundable(fundable: FundableWithoutId) extends Command

case class CreateDonation(donation: DonationWithoutId) extends Command

case class CreateLedgerDonation(donation: DonationWithoutIdAndFrom) extends Command

case class ChangeDonaterName(donaterId: UUID, name: String) extends Command
