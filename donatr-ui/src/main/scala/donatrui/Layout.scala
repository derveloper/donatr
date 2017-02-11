package donatrui

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

  private def createLedgerDonation(event: Event): Unit = {
    currentDialog := Some(Components.DepositDialog())
  }

  val donaterNavBar: Elem = {
    <ul class={DonatrStyles.nav.htmlClass}>
      <li onclick={createDonater _} class={DonatrStyles.navItem.htmlClass}>+user</li>
    </ul>
  }

  val donatableNavBar: Elem = {
    <ul class={DonatrStyles.nav.htmlClass}>
      <li onclick={createDonatable _} class={DonatrStyles.navItem.htmlClass}>+item</li>
      <li onclick={createLedgerDonation _} class={DonatrStyles.navItem.htmlClass}>+€</li>
      <li class={DonatrStyles.navItem.htmlClass}>>funding</li>
    </ul>
  }

  val main: Elem =
    <div id="app">
      <h1 class={DonatrStyles.header.htmlClass}>donatr</h1>
      {currentDonater.map(d => Components.CurrentDonaterComponent(d))}
      {States.currentNav}
      <hr class={DonatrStyles.hr.htmlClass}/>
      {currentView}
    </div>
}
