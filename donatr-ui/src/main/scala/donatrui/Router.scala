package donatrui

import org.scalajs.dom
import org.scalajs.dom.{Event, PopStateEvent}

object Router {
  def init(routes: List[(String, (Map[String, String]) => Unit)]): Unit = {
    dom.window.onhashchange = (e) => {
      println(e)
      router(dom.window.location.hash, routes)
    }
    dom.window.addEventListener("popstate", (e: Event) => {
      val poppedRoute = Option.apply(e.asInstanceOf[PopStateEvent].state.asInstanceOf[String]).getOrElse("/")
      router(poppedRoute, routes)
    })
    router(dom.window.location.hash, routes)
  }

  def router(uRL: String, routes: List[(String, (Map[String, String]) => Unit)]): Unit = {
    val urlParts = uRL.replace("#", "").split("/")
    val matchingRoute = routes.filter { r =>
      val routeParts = r._1.split("/").zipWithIndex.filter(!_._1.startsWith(":"))
      routeParts.forall(e => e._1 == urlParts(e._2))
    }.head

    val paramParts = matchingRoute._1.split("/").zipWithIndex.filter(_._1.startsWith(":"))
    val params = paramParts.toList.map { p =>
      p._1.replace(":", "") -> urlParts(p._2)
    }.toMap

    //noinspection ScalaUselessExpression
    matchingRoute._2(params)
  }
}
