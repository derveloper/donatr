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
    <ul class={DonatrStyles.nav.htmlClass}>
      <li onclick={createDonater _} class={DonatrStyles.navItem.htmlClass}>+user</li>
    </ul>
  }

  def donatableNavBar: Elem = {
    <ul class={DonatrStyles.nav.htmlClass}>
      <li onclick={createDonatable _} class={DonatrStyles.navItem.htmlClass}>+item</li>
      <li onclick={deposit _} class={DonatrStyles.navItem.htmlClass}>+€</li>
      <li class={DonatrStyles.navItem.htmlClass}>
        {currentDonater.map {
          case Some(d) => <a href={s"#/${d.id}/fundables"}>&lt;funding</a>
          case _ => <span/>
      }}
      </li>
      <li>x{ currentMultiplicator.map { m => {
          <input value={s"$m"}
                 type="number"
                 onblur={inputEvent(currentMultiplicator := _.value.toInt)} />}}}
      </li>
    </ul>
  }

  def fundableNavBar: Elem = {
    <ul class={DonatrStyles.nav.htmlClass}>
      <li onclick={createFundable _} class={DonatrStyles.navItem.htmlClass}>+fundable</li>
      <li onclick={deposit _} class={DonatrStyles.navItem.htmlClass}>+€</li>
      <li class={DonatrStyles.navItem.htmlClass}>
        {currentDonater.map {
          case Some(d) => <a href={s"#/${d.id}/donatables"}>&lt;items</a>
          case _ => <span/>
      }}
      </li>
    </ul>
  }

  def main: Elem = {
    <div id="app">
      <h1 class={DonatrStyles.header.htmlClass}>donatr</h1>
      {currentDonater.map(d => Components.CurrentDonaterComponent(d))}
      {States.currentNav}
      <hr class={DonatrStyles.hr.htmlClass}/>
      {currentView}
    </div>
  }
}
