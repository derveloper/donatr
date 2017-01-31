package donatr

case class NameTaken() extends Exception {
  override def getMessage: String = s"Name is already taken."
}

case class UnknownCommand(cmd: Command) extends Exception {
  override def getMessage: String = s"Unknown command $cmd."
}

case class UnknownEvent(event: Event) extends Exception {
  override def getMessage: String = s"Unknown event $event."
}

case class UnknownEntity(entity: Any) extends Exception {
  override def getMessage: String = s"Unknown entity $entity."
}

case class DonatrNotFound() extends Exception {
  override def getMessage: String = "Unknown entity"
}
