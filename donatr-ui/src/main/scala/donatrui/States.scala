package donatrui

import donatrui.Api.{Donater, DonaterUpdatedEvent}
import mhtml.{Cancelable, Var}

import scala.scalajs.js
import scala.util.Success
import scala.xml.Elem

object States {
  val currentDonater: Var[Option[Donater]] = Var(None)
  val currentNav: Var[Elem] = Var(Layout.donaterNavBar)
  val currentView = Var(<span/>)

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

  def updateState(eventType: Symbol, obj: js.Object): Unit = {
    if (eventType == 'DonaterUpdated) {
      updateState(obj.asInstanceOf[DonaterUpdatedEvent])
    }
  }
}
