package donatr

case class DonatableNameTaken() extends Exception {
  override def getMessage: String = s"Name is already taken."
}

case class UnknownCommand(cmd: Command) extends Exception {
  override def getMessage: String = s"Unknown command $cmd."
}
