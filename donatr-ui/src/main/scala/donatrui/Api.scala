package donatrui

import mhtml.{Rx, Var}
import org.scalajs.dom.ext.Ajax
import org.scalajs.dom.ext.Ajax.InputData

import scala.concurrent.Future
import scala.scalajs.js
import scala.scalajs.js.annotation.JSName
import scala.scalajs.js.{Array, JSON}
import scala.util.{Success, Try}

object Api {
  import scala.concurrent.ExecutionContext.Implicits.global

  @js.native
  trait Donation extends js.Object {
    val from: String = js.native
    val to: String = js.native
    val value: Float = js.native
  }

  @js.native
  trait Donater extends js.Object {
    val id: String = js.native
    val name: String = js.native
    val email: String = js.native
    val balance: Float = js.native
  }

  @js.native
  trait Donatable extends js.Object {
    val id: String = js.native
    val name: String = js.native
    val minDonationAmount: Float = js.native
  }

  @js.native
  trait DonatrEvent extends js.Object

  @js.native
  @JSName("DonaterUpdated")
  class DonaterUpdated extends DonatrEvent {
    val donater: Donater = js.native
  }

  @js.native
  @JSName("DonaterUpdatedEvent")
  class DonaterUpdatedEvent extends DonatrEvent {
    val DonaterUpdated: DonaterUpdated = js.native
  }

  @js.native
  @JSName("DonaterCreated")
  class DonaterCreated extends DonatrEvent {
    val donater: Donater = js.native
  }

  @js.native
  @JSName("DonaterCreatedEvent")
  class DonaterCreatedEvent extends DonatrEvent {
    val DonaterCreated: DonaterCreated = js.native
  }

  def fromFuture[T](future: Future[T]): Rx[Option[Try[T]]] = {
    val result = Var(Option.empty[Try[T]])
    future.onComplete(x => result := Some(x))
    result
  }

  def doGetRequest[T](url: String)(f: String => T): Rx[Option[Try[T]]] =
    fromFuture(Ajax.get(s"$url"))
      .map(_.map(_.withFilter(_.status == 200).map { x =>
        f(x.responseText)
      }))

  def doPostRequest[T](url: String, data: Ajax.InputData)(f: String => T): Rx[Option[Try[T]]] =
    fromFuture(Ajax.post(s"$url", data))
      .map(_.map(_.withFilter(_.status == 200).map { x =>
        f(x.responseText)
      }))

  def createDonater(name: String, email: String): Unit = {
    post("/api/donaters", InputData.str2ajax(JSON.stringify(js.Dynamic.literal(
      name = name,
      email = email,
      balance = 0
    ))), f => f)
  }

  def fetchDonater(id: String): Rx[Option[Try[Donater]]] = {
    fetch(s"/api/donaters/$id", f => f.asInstanceOf[Donater])
  }

  def fetchDonaters: Rx[List[Donater]] = {
    fetch("/api/donaters", f => f.asInstanceOf[Array[Donater]].toList)
        .map {
          case Some(Success(donaters)) => donaters
          case _ => List.empty
        }
  }

  def fetchDonatables: Rx[Option[Try[List[Donatable]]]] = {
    fetch("/api/donatables", f => f.asInstanceOf[Array[Donatable]].toList)
  }

  def donate(donater: Donater, donatable: Donatable): Rx[Option[Try[js.Dynamic]]] = {
    post("/api/donations", InputData.str2ajax(JSON.stringify(js.Dynamic.literal(
      from = donater.id,
      to = donatable.id,
      value = donatable.minDonationAmount
    ))), f => f)
  }

  private def fetch[Out](url: String, f: js.Dynamic => Out) = {
    doGetRequest(s"http://localhost:8080$url")(s => JSON.parse(s))
      .map { d => d.map(d2 => d2.map(d3 => f(d3))) }
  }

  private def post[Out](url: String, data: Ajax.InputData, f: js.Dynamic => Out) = {
    doPostRequest(s"http://localhost:8080$url", data)(s => JSON.parse(s))
      .map { d => d.map(d2 => d2.map(d3 => f(d3))) }
  }
}
