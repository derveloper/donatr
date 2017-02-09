package donatrui

object Layout {
  val main =
    <div id="app">
      <h1 class={DonatrStyles.header.htmlClass}>donatr</h1>{States.currentDonater.map(d => {
      if (d == null) {
        <div class={DonatrStyles.currentDonater.htmlClass}>
          select user
        </div>
      }
      else {
        Components.CurrentDonaterComponent(d)
      }
    })}<ul class={DonatrStyles.nav.htmlClass}>
      <li class={DonatrStyles.navItem.htmlClass}>+user</li>
    </ul>
      <hr class={DonatrStyles.hr.htmlClass}/>{States.currentView}
    </div>
}
