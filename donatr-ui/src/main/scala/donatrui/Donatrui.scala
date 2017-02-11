package donatrui

import mhtml._
import org.scalajs.dom

import scala.language.{implicitConversions, postfixOps}
import scala.scalajs.js.JSApp
import scalacss.Defaults._


object Donatrui extends JSApp {
  def main(): Unit = {
    mount(dom.document.head, <style>{DonatrStyles.renderA[String]}</style>)
    mount(dom.document.getElementById("root"), Layout.main)
    mount(dom.document.body, Components.BaseDialog())
    Router.init(Routes.routes)
    Websocket.init()
  }
}
