package donatrui


import donatrui.States._

import scala.language.implicitConversions
import scala.xml.Elem

case class Bool(b: Boolean) {
  def :?[X](t: => X) = new {
    if(b) t
  }
}

object Bool {
  implicit def BooleanBool(b: Boolean): Bool = Bool(b)
}

object Routes {
  import Bool._

  def updateCurrentView(view: Elem, after: Unit => Unit = () => _): Unit = {
    currentView.update(_ => view)
    after(())
  }

  def donatablesRoute(params: Map[String, String]): Unit = {
    println("donatablesRoute")
    donatables.value.isEmpty :? Api.fetchDonatables.foreach(l => donatables := l)
    Views.donatablesView()
      .foreach(updateCurrentView(_, _ => {println("each donatableview"); States.setCurrentDonater(params("donaterId"))}))
    currentNav := Layout.donatableNavBar
  }

  def fundablesRoute(params: Map[String, String]): Unit = {
    fundables.value.isEmpty :? Api.fetchFundables.foreach(l => fundables := l)
    Views.fundablesView()
      .foreach(updateCurrentView(_, _ => States.setCurrentDonater(params("donaterId"))))
    currentNav := Layout.fundableNavBar
  }

  def donatersRoute(params: Map[String, String]): Unit = {
    donaters.value.isEmpty :? Api.fetchDonaters.foreach(l => donaters := l)
    Views.donatersView()
      .foreach(updateCurrentView(_, _ => States.unsetCurrentDonater()))
    currentNav := Layout.donaterNavBar
  }

  lazy val routes: List[(String, (Map[String, String]) => Unit)] = List(
    "/:donaterId/donatables" -> Routes.donatablesRoute,
    "/:donaterId/fundables" -> Routes.fundablesRoute,
    "/" -> Routes.donatersRoute
  )
}
