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
    println(s"update view ${view.getClass.getName}")
    currentView.update(_ => view)
    after(())
  }

  def donatablesRoute(params: Map[String, String]): Unit = {
    donatables.value.isEmpty :? Api.fetchDonatables.foreach(l => donatables := l)
    Views.donatablesView()
      .foreach(updateCurrentView(_, _ => States.setCurrentDonater(params("donaterId"))))
  }

  def donatersRoute(params: Map[String, String]): Unit = {
    donaters.value.isEmpty :? Api.fetchDonaters.foreach(l => donaters := l)
    Views.donatersView()
      .foreach(updateCurrentView(_, _ => States.unsetCurrentDonater()))
  }

  val routes: List[(String, (Map[String, String]) => Unit)] = List(
    "/:donaterId/donatables" -> Routes.donatablesRoute,
    "/" -> Routes.donatersRoute
  )
}
