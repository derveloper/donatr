package donatrui

import donatrui.Api._
import mhtml._
import org.scalajs.dom
import org.scalajs.dom.Event
import org.scalajs.dom.raw.WebSocket

import scala.language.{implicitConversions, postfixOps}
import scala.scalajs.js.JSApp
import scala.util.Success
import scala.xml.Elem
import scalacss.Defaults._

object MyStyles extends StyleSheet.Inline {

  import dsl._

  val header = style(
    textAlign.center
  )

  val commonBorder = mixin(
    border(2 px, solid, rgb(0, 255, 0))
  )

  val donater = style(
    commonBorder,
    display.inlineBlock,
    margin(1 rem),
    &.hover(
      cursor.pointer
    ),
    &.active(
      backgroundColor(rgba(0, 255, 0, 0.3))
    )
  )

  val currentDonater = style(
    commonBorder,
    width(80 vw),
    margin(0 px, auto),
    fontSize(1.4 rem),
    padding(0.8 rem),
    &.active(
      backgroundColor(rgba(0, 255, 0, 0.3))
    )
  )

  val donaterName = style(
    fontSize(1.2 rem),
    paddingLeft(0.5 rem),
    paddingBottom(0.3 rem)
  )

  val nav = style()
  val navItem = style(
    display.inlineBlock,
    listStyleType := "none",
    fontSize(1.4 rem)
  )
  val hr = style(border(1 px, solid, rgb(0, 255, 0)))
}

object Donatrui extends JSApp {
  val currentView = Var(<span/>)
  val currentDonater = Var[Donater](null)

  private def md5(string: String) = {
    import scala.scalajs.js
    val spark = js.Dynamic.newInstance(js.Dynamic.global.SparkMD5)()
    spark.append(string)
    spark.end().asInstanceOf[String]
  }

  private def DonaterComponent(donater: Donater) = {
    def onClick: (Event) => Unit = { event: Event => currentDonater.:=(donater) }

    <div class={MyStyles.donater.htmlClass}>
      <a onclick={onClick} href={s"#/${donater.id}/donatables"}>
        <img src={s"https://www.gravatar.com/avatar/${md5(donater.email)}?s=115"}/>
        <div class={MyStyles.donaterName.htmlClass}>
          {donater.name}
        </div>
      </a>
    </div>
  }

  private def DonatableComponent(donatable: Donatable) = {
    def onClick: (Event) => Unit = { event: Event => {
      Api.donate(currentDonater.value, donatable)
    }
    }

    <div onclick={onClick} class={MyStyles.donater.htmlClass}>
      <img src={s"https://www.gravatar.com/avatar/${md5(donatable.name)}?s=115"}/>
      <div class={MyStyles.donaterName.htmlClass}>
        {donatable.name}
      </div>
    </div>
  }

  private def donatersView(): Rx[Elem] =
    Api.fetchDonaters
      .map {
        case None => <div>Loading</div>
        case Some(Success(donaters)) =>
          <div>
            {donaters.map(d => DonaterComponent(d))}
          </div>
        case _ => <div>Failure!</div>
      }

  private def donatablesView(): Rx[Elem] =
    Api.fetchDonatables
      .map {
        case None => <div>Loading</div>
        case Some(Success(donaters)) =>
          <div>
            {donaters.map(d => DonatableComponent(d))}
          </div>
        case _ => <div>Failure!</div>
      }

  private def initRouter() = {
    dom.window.onhashchange = (e) => {
      router(e.newURL)
    }
    router(dom.window.location.href)
  }

  private def router(uRL: String) = {
    if (uRL.contains("donatables"))
      donatablesView().foreach(f => {
        currentView.update(_ => f)
      })
    else {
      donatersView().foreach(f => {
        currentView.update(_ => f)
      })
    }
  }

  private def currentDonaterViewRenderer(donater: Donater) = {
    <div class={MyStyles.currentDonater.htmlClass}>
      {donater.name}{donater.balance}
    </div>
  }

  val app =
    <div id="app">
      <h1 class={MyStyles.header.htmlClass}>donatr</h1>{currentDonater.map(d => {
      if (d == null) {
        <div class={MyStyles.currentDonater.htmlClass}>
          select user
        </div>
      }
      else {
        currentDonaterViewRenderer(d)
      }
    })}<ul class={MyStyles.nav.htmlClass}>
      <li class={MyStyles.navItem.htmlClass}>+user</li>
    </ul>
      <hr class={MyStyles.hr.htmlClass}/>{currentView}
    </div>

  private def updateState(event: DonatrEvent) = {
    if (event.isInstanceOf[DonaterUpdated]) {

    }
  }

  private def initWebsocket(): Unit = {
    val ws = new WebSocket("ws://localhost:8080/events")
    ws.onmessage = (msg) => {
      println(msg.data)
    }
    ws.onclose = (e) => initWebsocket()
  }

  def main(): Unit = {
    initRouter()
    initWebsocket()
    mount(dom.document.head, <style>
      {MyStyles.renderA[String]}
    </style>)
    mount(dom.document.getElementById("root"), app)
  }
}
