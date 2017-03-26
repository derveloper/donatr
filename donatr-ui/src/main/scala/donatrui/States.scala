package donatrui

import donatrui.Api._
import mhtml.Var

import scala.scalajs.js
import scala.util.Success
import scala.xml.Elem

object States {
  import scala.collection.immutable.ListMap

  val donaters: Var[ListMap[String, Donater]] = Var(ListMap.empty)
  val donatables: Var[List[Donatable]] = Var(List.empty)
  val fundables: Var[List[Fundable]] = Var(List.empty)
  val currentDonater: Var[Option[Donater]] = Var(None)
  val currentNav: Var[Elem] = Var(Layout.donaterNavBar)
  val currentView = Var(<span/>)
  val currentDialog: Var[Option[Elem]] = Var(None)
  val currentMultiplicator: Var[Int] = Var(1)

  def setCurrentDonater(donaterId: String): Unit = {
    if ((currentDonater.impure.value.nonEmpty
        && currentDonater.impure.value.get.id != donaterId)
    || currentDonater.impure.value.isEmpty) {
      if (donaters.impure.value.contains(donaterId)) {
        currentDonater := Some(donaters.impure.value(donaterId))
      }
      else {
        Api.fetchDonater(donaterId).impure.foreach {
          case None => None
          case Some(Success(donater)) =>
            currentDonater := Some(donater)
          case _ => None
        }.cancel
      }
    }
  }

  def unsetCurrentDonater(): Unit = {
    currentDonater := None
  }

  def updateState(event: DonaterUpdatedEvent): Unit = {
    currentDonater := Some(event.DonaterUpdated.donater)
  }

  def updateState(event: DonaterCreatedEvent): Unit = {
    donaters := donaters.impure.value + (event.DonaterCreated.donater.id -> event.DonaterCreated.donater)
  }

  def updateState(event: DonatableCreatedEvent): Unit = {
    donatables := donatables.impure.value :+ event.DonatableCreated.donatable
  }

  def updateState(event: FundableCreatedEvent): Unit = {
    fundables := fundables.impure.value :+ event.FundableCreated.fundable
  }

  def updateState(event: FundableUpdatedEvent): Unit = {
    fundables := fundables.impure.value.map { f =>
      if (f.id == event.FundableUpdated.fundable.id) event.FundableUpdated.fundable
      else f
    }
  }

  def updateState(eventType: Symbol, obj: js.Object): Unit = eventType match {
    case 'DonaterUpdated =>
      updateState(obj.asInstanceOf[DonaterUpdatedEvent])
    case 'DonaterCreated =>
      updateState(obj.asInstanceOf[DonaterCreatedEvent])
    case 'DonatableCreated =>
      updateState(obj.asInstanceOf[DonatableCreatedEvent])
    case 'FundableCreated =>
      updateState(obj.asInstanceOf[FundableCreatedEvent])
    case 'FundableUpdated =>
      updateState(obj.asInstanceOf[FundableUpdatedEvent])
    case _ =>
  }
}
