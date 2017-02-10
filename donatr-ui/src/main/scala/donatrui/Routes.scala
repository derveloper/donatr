package donatrui

import mhtml.Var

import scala.xml.Elem

object Routes {
  import States._
  val currentView = Var(<span/>)

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
    Views.donatersView()
      .foreach(updateCurrentView(_, _ => currentDonater := Left(())))
  }

  val routes: List[(String, (Map[String, String]) => Unit)] = List(
    "/:donaterId/donatables" -> Routes.donatablesRoute,
    "/" -> Routes.donatersRoute
  )
}
