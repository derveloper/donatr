package donatrui


import scala.language.implicitConversions
import scala.xml.Elem

import mhtml.Rx

case class Bool(b: Boolean) {
  def :?[X](t: => X): Object = new {
    if (b) t
  }
}

object Bool {
  implicit def BooleanBool(b: Boolean): Bool = Bool(b)
}

object Routes {
  import donatrui.States._

  import scala.collection.immutable.ListMap

  def updateCurrentView(view: Elem, after: Unit => Unit = () => _): Unit = {
    println("updateCurrentView")
    currentView.update(_ => view)
    after(())
  }

  def donatablesRoute(params: Map[String, String]): Rx[Elem] = {
    Api.fetchDonatables.map(l => donatables := l)
    println("donatablesRoute")
    currentNav := Layout.donatableNavBar
    currentDonaterId := Some(params("donaterId"))
    Views.donatablesView()
  }

  def fundablesRoute(params: Map[String, String]): Rx[Elem] = {
    Api.fetchFundables.map(l => fundables := l)
    currentNav := Layout.fundableNavBar
    Views.fundablesView()
  }

  def donatersRoute(params: Map[String, String]): Rx[Elem] = {
    println("donatersRoute1")
    Api.fetchDonaters.map(l => {
      println(l)
      donaters := ListMap(l.map(e => e.id -> e):_*)
    })
    currentNav := Layout.donaterNavBar
    currentDonaterId := None
    Views.donatersView()
  }

  lazy val routes: List[(String, (Map[String, String]) => Rx[Elem])] = List(
    "/:donaterId/donatables" -> Routes.donatablesRoute,
    "/:donaterId/fundables" -> Routes.fundablesRoute,
    "/" -> Routes.donatersRoute
  )
}
