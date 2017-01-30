package donatr

sealed trait Command

case class CreateDonatable(donatable: Donatable) extends Command
case class CreateFixedValueDonatable(donatable: FixedValueDonatable) extends Command
