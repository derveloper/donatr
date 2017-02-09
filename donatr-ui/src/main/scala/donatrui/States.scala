package donatrui

import donatrui.Api.{Donater, DonaterUpdatedEvent}
import mhtml.{Cancelable, Var}

import scala.scalajs.js
import scala.util.Success
import scala.xml.Elem

object States {
  val currentNav: Var[Elem] = Var(Layout.donaterNavBar)
  val currentView: Var[Elem] = Var(<span/>)
  val currentDonater: Var[Either[Unit, Donater]] = Var[Either[Unit, Donater]](Left(()))

  def setCurrentDonater(donaterId: String): Cancelable = {
    Api.fetchDonater(donaterId).foreach {
      case None => println("loading")
      case Some(Success(donater)) =>
        States.currentDonater := Right(donater)
      case _ => println("Failure!")
    }
  }

  def updateState(event: DonaterUpdatedEvent): Unit = {
    States.currentDonater := Right(event.DonaterUpdated.donater)
  }

  def updateState(eventType: Symbol, obj: js.Object): Unit = {
    if (eventType == 'DonaterUpdated) {
      updateState(obj.asInstanceOf[DonaterUpdatedEvent])
    }
  }
}
