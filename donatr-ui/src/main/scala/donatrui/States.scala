package donatrui

import donatrui.Api._
import mhtml.{Cancelable, Var}

import scala.scalajs.js
import scala.util.Success
import scala.xml.Elem

object States {
  val donaters: Var[List[Donater]] = Var(List.empty)
  val donatables: Var[List[Donatable]] = Var(List.empty)
  val currentDonater: Var[Option[Donater]] = Var(None)
  val currentNav: Var[Elem] = Var(Layout.donaterNavBar)
  val currentView = Var(<span/>)
  val currentDialog: Var[Option[Elem]] = Var(None)

  def setCurrentDonater(donaterId: String): Cancelable = {
    Api.fetchDonater(donaterId).foreach {
      case None => println("loading")
      case Some(Success(donater)) =>
        currentDonater := Some(donater)
      case _ => println("Failure!")
    }
  }

  def unsetCurrentDonater(): Unit = {
    currentDonater := None
  }

  def updateState(event: DonaterUpdatedEvent): Unit = {
    currentDonater := Some(event.DonaterUpdated.donater)
  }

  def updateState(event: DonaterCreatedEvent): Unit = {
    donaters := donaters.value :+ event.DonaterCreated.donater
  }

  def updateState(event: DonatableCreatedEvent): Unit = {
    donatables := donatables.value :+ event.DonatableCreated.donatable
  }

  def updateState(eventType: Symbol, obj: js.Object): Unit = eventType match {
    case 'DonaterUpdated =>
      updateState(obj.asInstanceOf[DonaterUpdatedEvent])
    case 'DonaterCreated =>
      updateState(obj.asInstanceOf[DonaterCreatedEvent])
    case 'DonatableCreated =>
      updateState(obj.asInstanceOf[DonatableCreatedEvent])
  }
}
