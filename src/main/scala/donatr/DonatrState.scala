package donatr

import scala.collection.immutable.List

case class DonatrState(
                        donatables: Seq[BaseDonatable] = List.empty,
                        transactions: Seq[Transaction] = List.empty
                      ) {

  def handleCreate(event: DonatableCreated): DonatrState = {
    donatables.count(d => d.name == event.donatable.name) match {
      case 0 =>
        copy(donatables :+ event.donatable)
      case _ =>
        this
    }
  }

  def apply(event: Event): DonatrState = event match {
    case DonatableCreated(Donatable(id, name, balance)) =>
      handleCreate(DonatableCreated(Donatable(id, name, balance)))
    case _ => this
  }
}
