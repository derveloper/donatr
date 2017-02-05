package donatr

trait EventPublisher {
  def publish(event: Event): Unit
}
