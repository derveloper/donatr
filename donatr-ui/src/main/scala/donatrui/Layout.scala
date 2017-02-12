package donatrui

import donatrui.Api.Fundable
import org.scalajs.dom.Event

import scala.xml.Elem

object Layout {
  import States._

  private def createDonater(event: Event): Unit = {
    currentDialog := Some(Components.CreateDonaterDialog())
  }

  private def createDonatable(event: Event): Unit = {
    currentDialog := Some(Components.CreateDonatableDialog())
  }

  private def createFundable(event: Event): Unit = {
    currentDialog := Some(Components.CreateFundableDialog())
  }

  private def deposit(event: Event): Unit = {
    currentDialog := Some(Components.DepositDialog())
  }

  def fund(event: Event, fundable: Fundable): Unit = {
    currentDialog := Some(Components.FundDialog(fundable))
  }

  def donaterNavBar: Elem = {
    <ul class={"DonatrStyles-nav"}>
      <li onclick={createDonater _} class={"DonatrStyles-navItem"}>+user</li>
    </ul>
  }

  def donatableNavBar: Elem = {
    <ul class={"DonatrStyles-nav"}>
      <li onclick={createDonatable _} class={"DonatrStyles-navItem"}>+item</li>
      <li onclick={deposit _} class={"DonatrStyles-navItem"}>+€</li>
      <li class={"DonatrStyles-navItem"}>
        {currentDonater.map {
          case Some(d) => <a href={s"#/${d.id}/fundables"}>&gt;funding</a>
          case _ => <span/>
      }}
      </li>
      <li class={"DonatrStyles-navItem"}>x{ currentMultiplicator.map { m => {
          <input class="multiplicator" value={s"$m"}
                 type="number"
                 onblur={inputEvent(currentMultiplicator := _.value.toInt)} />}}}
      </li>
    </ul>
  }

  def fundableNavBar: Elem = {
    <ul class={"DonatrStyles-nav"}>
      <li onclick={createFundable _} class={"DonatrStyles-navItem"}>+fundable</li>
      <li onclick={deposit _} class={"DonatrStyles-navItem"}>+€</li>
      <li class={"DonatrStyles-navItem"}>
        {currentDonater.map {
          case Some(d) => <a href={s"#/${d.id}/donatables"}>&lt;items</a>
          case _ => <span/>
      }}
      </li>
    </ul>
  }

  def main: Elem = {
    <div id="app">
      <h1 class={"DonatrStyles-header"}><a href="#/">donatr</a></h1>
      {currentDonater.map(d => Components.CurrentDonaterComponent(d))}
      {States.currentNav}
      <hr class={"DonatrStyles-hr"}/>
      {currentView}
    </div>
  }
}
