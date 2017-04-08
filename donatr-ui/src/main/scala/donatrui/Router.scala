package donatrui

import mhtml.Rx
import org.scalajs.dom
import org.scalajs.dom.{Event, PopStateEvent}

import scala.xml.Elem

object Router {
  def init(routes: List[(String, (Map[String, String]) => Rx[Elem])]): Unit = {
    println("router init")
    dom.window.onhashchange = (e) => {
      dom.console.log("onhashchange")
      States.currentRoute := dom.window.location.hash
    }
    dom.window.addEventListener("popstate", (e: Event) => {
      println("popstate")
      val poppedRoute = Option.apply(e.asInstanceOf[PopStateEvent].state.asInstanceOf[String]).getOrElse("/")
      States.currentRoute := poppedRoute
    })
    States.currentRoute := dom.window.location.pathname
  }

  def route(uRL: String, routes: List[(String, (Map[String, String]) => Rx[Elem])]): Rx[Elem] = {
    val urlParts = uRL.replace("#", "").split("/")
    val matchingRoute = routes.filter { r =>
      val routeParts = r._1.split("/").zipWithIndex.filter(!_._1.startsWith(":"))
      if (urlParts.isEmpty)
        routeParts.forall(e => e._1 == "/")
      else
        routeParts.forall(e => e._1 == urlParts(e._2))
    }.head

    val paramParts = matchingRoute._1.split("/").zipWithIndex.filter(_._1.startsWith(":"))
    val params = paramParts.toList.map { p =>
      p._1.replace(":", "") -> urlParts(p._2)
    }.toMap

    println(s"rendering ${matchingRoute._1}")
    matchingRoute._2(params)
  }
}
