package donatr

import java.util.UUID

import io.vertx.scala.core.Vertx
import io.vertx.scala.core.http.{HttpClient, HttpClientOptions}
import org.scalacheck.Gen

object Benchmark {
  import scala.concurrent.Promise
  import scala.concurrent.duration.Duration
  import io.circe.generic.auto._
  import io.circe.syntax._
  import scala.concurrent.Await

  def main(args: Array[String]): Unit = {
    val vertx = Vertx.vertx()
    val options = HttpClientOptions().setDefaultHost("donatr.herokuapp.com").setSsl(true).setDefaultPort(443)
    val client = vertx.createHttpClient(options)

    val donaters = createDonaters(Range(1, 10).map(_ => donaterGen.sample.get), client)

    val donatables = createDonatables(Range(1, 35).map(_ => donatableGen.sample.get), client)

    Range(1,1000).foreach { _ =>
      val donations = util.Random.shuffle(donaters).zip(util.Random.shuffle(donatables)).map { p =>
        DonationWithoutId(UUID.fromString(p._1), UUID.fromString(p._2), Gen.chooseNum(-10.0, 10.0).sample.get)
      }

      createDonations(donations, client)
    }
  }

  val donaterGen = for {
    name <- Gen.alphaStr
    email <- Gen.numStr
    balance <- Gen.chooseNum(-10.0, 10.0)
  } yield DonaterWithoutId(name.take(4), email.take(4), balance)

  val donatableGen = for {
    name <- Gen.alphaStr
    balance <- Gen.chooseNum(-10.0, 10.0)
    minDonationAmount <- Gen.chooseNum(-10.0, 10.0)
  } yield DonatableWithoutId(name.take(4), balance, minDonationAmount)

  def noop(a: Any): Unit = {}

  def createDonations(donations: Seq[DonationWithoutId], client: HttpClient): Unit = {
    donations.foreach(d => {
      client.post("/api/donations")
        .handler(noop)
        .end(d.asJson.noSpaces)
    })
  }

  def createDonaters(donaters: Seq[DonaterWithoutId], client: HttpClient): Seq[String] = {
    donaters.map(d => {
      val p = Promise[String]()
      val f = p.future
      client.post("/api/donaters")
        .handler(r => p.success(r.getHeader("Location").getOrElse("")))
        .end(d.asJson.noSpaces)
      f
    }).map(f => Await.result(f, Duration.Inf))
      .filter(_.nonEmpty)
      .map(s => {
        val parts = s.split("/")
        parts(parts.length-1)
      })
  }

  def createDonatables(donatables: Seq[DonatableWithoutId], client: HttpClient): Seq[String] = {
    donatables.map(d => {
      val p = Promise[String]()
      val f = p.future
      client.post("/api/donatables")
        .handler(r => p.success(r.getHeader("Location").getOrElse("")))
        .end(d.asJson.noSpaces)
      f
    }).map(f => Await.result(f, Duration.Inf))
      .filter(_.nonEmpty)
      .map(s => {
        val parts = s.split("/")
        parts(parts.length-1)
      })
  }
}
