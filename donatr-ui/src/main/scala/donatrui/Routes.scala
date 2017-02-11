package donatrui


import donatrui.States._

import scala.xml.Elem

object Routes {
  def updateCurrentView(view: Elem, after: Unit => Unit = () => _): Unit = {
    println(s"update view ${view.getClass.getName}")
    currentView.update(_ => view)
    after(())
  }

  def donatablesRoute(params: Map[String, String]): Unit = {
    Views.donatablesView()
      .foreach(updateCurrentView(_, _ => States.setCurrentDonater(params("donaterId"))))
  }

  def donatersRoute(params: Map[String, String]): Unit = {
    Api.fetchDonaters.foreach(l => donaters := l)
    Views.donatersView()
      .foreach(updateCurrentView(_, _ => States.unsetCurrentDonater()))
  }

  val routes: List[(String, (Map[String, String]) => Unit)] = List(
    "/:donaterId/donatables" -> Routes.donatablesRoute,
    "/" -> Routes.donatersRoute
  )
}
