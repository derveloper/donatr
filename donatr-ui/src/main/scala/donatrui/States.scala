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
  val currentRoute = Var("/")
  val currentDialog: Var[Option[Elem]] = Var(None)
  val currentMultiplicator: Var[Int] = Var(1)

  def setCurrentDonater(donaterId: String): Unit = {
    Api.fetchDonater(donaterId).impure.foreach {
      case None => None
      case Some(Success(donater)) =>
        currentDonater := Some(donater)
      case _ => None
    }.cancel
  }

  def unsetCurrentDonater(): Unit = {
    currentDonater := None
  }

  def updateState(event: DonaterUpdatedEvent): Unit = {
    currentDonater := Some(event.DonaterUpdated.donater)
  }

  def updateState(event: DonaterCreatedEvent): Unit = {
    donaters.update(d => d + (event.DonaterCreated.donater.id -> event.DonaterCreated.donater))
  }

  def updateState(event: DonatableCreatedEvent): Unit = {
    donatables.update(d => d :+ event.DonatableCreated.donatable)
  }

  def updateState(event: FundableCreatedEvent): Unit = {
    fundables.update(f => f :+ event.FundableCreated.fundable)
  }

  def updateState(event: FundableUpdatedEvent): Unit = {
    fundables.update(f => f.map { f2 =>
      if (f2.id == event.FundableUpdated.fundable.id) event.FundableUpdated.fundable
      else f2
    })
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
