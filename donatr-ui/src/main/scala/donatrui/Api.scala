package donatrui

import mhtml.{Rx, Var}
import org.scalajs.dom.ext.Ajax
import org.scalajs.dom.ext.Ajax.InputData

import scala.concurrent.Future
import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobal
import scala.scalajs.js.{Array, JSON}
import scala.util.{Success, Try}
import scala.concurrent.ExecutionContext.Implicits.global

object Api {
  @js.native
  trait Donation extends js.Object {
    val from: String = js.native
    val to: String = js.native
    val value: Double = js.native
  }

  @js.native
  trait Donater extends js.Object {
    val id: String = js.native
    val name: String = js.native
    val email: String = js.native
    val balance: Double = js.native
  }

  @js.native
  trait Donatable extends js.Object {
    val id: String = js.native
    val name: String = js.native
    val imageUrl: String = js.native
    val minDonationAmount: Double = js.native
  }

  @js.native
  trait Fundable extends js.Object {
    val id: String = js.native
    val name: String = js.native
    val imageUrl: String = js.native
    val fundingTarget: Double = js.native
    val balance: Double = js.native
  }

  @js.native
  trait DonatrEvent extends js.Object

  @js.native
  @JSGlobal("DonaterUpdated")
  class DonaterUpdated extends DonatrEvent {
    val donater: Donater = js.native
  }

  @js.native
  @JSGlobal("DonaterUpdatedEvent")
  class DonaterUpdatedEvent extends DonatrEvent {
    val DonaterUpdated: DonaterUpdated = js.native
  }

  @js.native
  @JSGlobal("DonaterCreated")
  class DonaterCreated extends DonatrEvent {
    val donater: Donater = js.native
  }

  @js.native
  @JSGlobal("DonaterCreatedEvent")
  class DonaterCreatedEvent extends DonatrEvent {
    val DonaterCreated: DonaterCreated = js.native
  }

  @js.native
  @JSGlobal("DonatableCreated")
  class DonatableCreated extends DonatrEvent {
    val donatable: Donatable = js.native
  }

  @js.native
  @JSGlobal("DonatableCreatedEvent")
  class DonatableCreatedEvent extends DonatrEvent {
    val DonatableCreated: DonatableCreated = js.native
  }

  @js.native
  @JSGlobal("FundableUpdated")
  class FundableUpdated extends DonatrEvent {
    val fundable: Fundable = js.native
  }

  @js.native
  @JSGlobal("FundableUpdatedEvent")
  class FundableUpdatedEvent extends DonatrEvent {
    val FundableUpdated: FundableUpdated = js.native
  }

  @js.native
  @JSGlobal("FundableCreated")
  class FundableCreated extends DonatrEvent {
    val fundable: Fundable = js.native
  }

  @js.native
  @JSGlobal("FundableCreatedEvent")
  class FundableCreatedEvent extends DonatrEvent {
    val FundableCreated: FundableCreated = js.native
  }

  def doGetRequest[T](url: String)(f: js.Dynamic => T): Rx[Option[Try[T]]] =
    Utils
      .fromFuture(Ajax.get(url))
      .map(r => {
        println(r)
        r.map(r2 => {
          println(r2)
          r2.withFilter(_.status == 200).map { x =>
            val json = JSON.parse(x.responseText)
            println("JSON: " + json)
            f(json)
          }
        })
      })

  def doPostRequest[T](url: String, data: Ajax.InputData)(f: String => T): Rx[Option[Try[T]]] =
    Utils.fromFuture(Ajax.post(s"$url", data))
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

  def createDonatable(name: String, imageUrl: String, minDonationAmount: Double): Unit = {
    post("/api/donatables", InputData.str2ajax(JSON.stringify(js.Dynamic.literal(
      name = name,
      imageUrl = imageUrl,
      minDonationAmount = minDonationAmount,
      balance = 0
    ))), f => f)
  }

  def createFundable(name: String, imageUrl: String, fundingTarget: Double): Unit = {
    post("/api/fundables", InputData.str2ajax(JSON.stringify(js.Dynamic.literal(
      name = name,
      imageUrl = imageUrl,
      fundingTarget = fundingTarget,
      balance = 0
    ))), f => f)
  }

  def fetchDonater(id: String): Rx[Option[Donater]] = {
    doGetRequest(s"/api/donaters/$id")(f => {
      println(f)
      f.asInstanceOf[Donater]
    }).map {
      case Some(Success(donaters)) => Some(donaters)
      case _ => None
    }
  }

  def fetchDonaters: Rx[List[Donater]] = {
    doGetRequest("/api/donaters")(f => {
      println(f)
      f.asInstanceOf[Array[Donater]].toList
    }).map {
      case Some(Success(donaters)) => donaters
      case _ => List.empty
    }
  }

  def fetchDonatables: Rx[List[Donatable]] = {
    doGetRequest("/api/donatables")(f => f.asInstanceOf[Array[Donatable]].toList)
      .map {
        case Some(Success(donatables)) => donatables
        case _ => List.empty
      }
  }

  def fetchFundables: Rx[List[Fundable]] = {
    doGetRequest("/api/fundables")(f => f.asInstanceOf[Array[Fundable]].toList)
      .map {
        case Some(Success(fundables)) => fundables
        case _ => List.empty
      }
  }

  def donate(donaterId: String, donatable: Donatable): Rx[Option[Try[js.Dynamic]]] = {
    post("/api/donations", InputData.str2ajax(JSON.stringify(js.Dynamic.literal(
      from = donaterId,
      to = donatable.id,
      value = donatable.minDonationAmount
    ))), f => f)
  }

  def donate(donater: Donater, donatable: Donatable): Rx[Option[Try[js.Dynamic]]] = {
    post("/api/donations", InputData.str2ajax(JSON.stringify(js.Dynamic.literal(
      from = donater.id,
      to = donatable.id,
      value = donatable.minDonationAmount
    ))), f => f)
  }

  def donate(donater: Donater, fundable: Fundable, amount: Double): Rx[Option[Try[js.Dynamic]]] = {
    post("/api/donations", InputData.str2ajax(JSON.stringify(js.Dynamic.literal(
      from = donater.id,
      to = fundable.id,
      value = amount
    ))), f => f)
  }

  def donate(donaterId: String, fundable: Fundable, amount: Double): Rx[Option[Try[js.Dynamic]]] = {
    post("/api/donations", InputData.str2ajax(JSON.stringify(js.Dynamic.literal(
      from = donaterId,
      to = fundable.id,
      value = amount
    ))), f => f)
  }

  def donate(donater: Donater, value: Double): Rx[Option[Try[js.Dynamic]]] = {
    post("/api/donations", InputData.str2ajax(JSON.stringify(js.Dynamic.literal(
      to = donater.id,
      value = value
    ))), f => f)
  }

  def donate(donaterId: String, value: Double): Rx[Option[Try[js.Dynamic]]] = {
    post("/api/donations", InputData.str2ajax(JSON.stringify(js.Dynamic.literal(
      to = donaterId,
      value = value
    ))), f => f)
  }

  private def post[Out](url: String, data: Ajax.InputData, f: js.Dynamic => Out) = {
    doPostRequest(url, data)(s => JSON.parse(s))
      .map { d => d.map(d2 => d2.map(d3 => f(d3))) }
  }
}
