package donatrui


import scala.language.implicitConversions
import scala.xml.Elem

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
  import Bool._

  def updateCurrentView(view: Elem, after: Unit => Unit = () => _): Unit = {
    currentView.update(_ => view)
    after(())
  }

  def donatablesRoute(params: Map[String, String]): Unit = {
    donatables.impure.value.isEmpty :? Api.fetchDonatables.map(l => donatables := l)
    Views.donatablesView()
      .map(updateCurrentView(_, _ => {
        States.setCurrentDonater(params("donaterId"))}))
    currentNav := Layout.donatableNavBar
  }

  def fundablesRoute(params: Map[String, String]): Unit = {
    fundables.impure.value.isEmpty :? Api.fetchFundables.map(l => fundables := l)
    Views.fundablesView()
      .map(updateCurrentView(_, _ => States.setCurrentDonater(params("donaterId"))))
    currentNav := Layout.fundableNavBar
  }

  def donatersRoute(params: Map[String, String]): Unit = {
    donaters.impure.value.isEmpty :? Api.fetchDonaters.collect(l =>
      donaters := ListMap(l.map(e => e.id -> e):_*))
    Views.donatersView()
      .map(updateCurrentView(_, _ => States.unsetCurrentDonater()))
    currentNav := Layout.donaterNavBar
  }

  lazy val routes: List[(String, (Map[String, String]) => Unit)] = List(
    "/:donaterId/donatables" -> Routes.donatablesRoute,
    "/:donaterId/fundables" -> Routes.fundablesRoute,
    "/" -> Routes.donatersRoute
  )
}
