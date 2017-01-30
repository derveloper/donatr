package donatr

sealed trait Command

case class CreateDonatable(donatable: Donatable) extends Command
