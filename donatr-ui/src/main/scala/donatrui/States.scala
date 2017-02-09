package donatrui

import donatrui.Api.{Donater, DonaterUpdatedEvent}
import mhtml.{Cancelable, Var}

import scala.scalajs.js
import scala.util.Success
import scala.xml.Elem

object States {
  val currentView: Var[Elem] = Var(<span/>)
  val currentDonater: Var[Donater] = Var[Donater](null)

  def setCurrentDonater(donaterId: String): Cancelable = {
    Api.fetchDonater(donaterId).foreach {
      case None => println("loading")
      case Some(Success(donater)) =>
        States.currentDonater := donater
      case _ => println("Failure!")
    }
  }

  def updateState(event: DonaterUpdatedEvent): Unit = {
    States.currentDonater := event.DonaterUpdated.donater
  }

  def updateState(eventType: Symbol, obj: js.Object): Unit = {
    if (eventType == 'DonaterUpdated) {
      updateState(obj.asInstanceOf[DonaterUpdatedEvent])
    }
  }
}
