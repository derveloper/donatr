package donatrui

import donatrui.Api.{Donater, DonaterUpdatedEvent}
import mhtml.{Cancelable, Var}

import scala.scalajs.js
import scala.util.Success
import scala.xml.Elem

object States {
  val currentDonater: Var[Either[Unit, Donater]] = Var(Left(()))
  val currentNav: Var[Elem] = Var(Layout.donaterNavBar)

  def setCurrentDonater(donaterId: String): Cancelable = {
    Api.fetchDonater(donaterId).foreach {
      case None => println("loading")
      case Some(Success(donater)) =>
        currentDonater := Right(donater)
      case _ => println("Failure!")
    }
  }

  def unsetCurrentDonater(): Unit = {
    currentDonater := Left(())
  }

  def updateState(event: DonaterUpdatedEvent): Unit = {
    currentDonater := Right(event.DonaterUpdated.donater)
  }

  def updateState(eventType: Symbol, obj: js.Object): Unit = {
    if (eventType == 'DonaterUpdated) {
      updateState(obj.asInstanceOf[DonaterUpdatedEvent])
    }
  }
}
