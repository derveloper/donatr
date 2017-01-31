package donatr

sealed trait Command

case class CreateDonater(donatable: DonaterWithoutId) extends Command
case class CreateDonatable(donatable: DonatableWithoutId) extends Command
case class CreateFundable(fundable: FundableWithoutId) extends Command
