package donatr

import java.net.URL

import io.vertx.core.json.{JsonArray, JsonObject}
import io.vertx.scala.core.Vertx
import io.vertx.scala.core.http.{HttpClient, HttpClientOptions}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future, Promise}

object DonatrToDonatr2 {
  import io.circe.generic.auto._
  import io.circe.syntax._

  def main(args: Array[String]): Unit = {
    val oldDonatrUrl = new URL("http://mete.fnord.fnet:80")
    val newDonatrUrl = new URL("http://donatr.fnord.fnet:80")
    val vertx = Vertx.vertx()
    val oldClient = vertx.createHttpClient(HttpClientOptions()
      .setDefaultHost(oldDonatrUrl.getHost)
      .setDefaultPort(oldDonatrUrl.getPort)
      .setLogActivity(true)
      .setConnectTimeout(1000))
    val newClient = vertx.createHttpClient(HttpClientOptions()
      .setDefaultHost(newDonatrUrl.getHost)
      .setDefaultPort(newDonatrUrl.getPort)
      .setLogActivity(true)
      .setConnectTimeout(1000))

    val oldPromise = Promise[JsonArray]()
    val oldFuture = oldPromise.future
    oldClient.get("/api/aggregate/account").handler { response =>
      response.bodyHandler { body =>
        oldPromise.success(body.toJsonObject.getJsonArray("accounts"))
      }
    }.end()

    val oldDonatablesPromise = Promise[JsonArray]()
    val oldDonatablesFuture = oldDonatablesPromise.future
    oldClient.get("/api/aggregate/donatable").handler { response =>
      response.bodyHandler { body =>
        oldDonatablesPromise.success(body.toJsonObject.getJsonArray("donatables"))
      }
    }.end()

    createDonaters(oldFuture, newClient)
    createDonatables(oldDonatablesFuture, newClient)
  }

  private def createDonatables(oldDonatablesFuture: Future[JsonArray], newClient: HttpClient) = {
    val oldDonatables = Await.result(oldDonatablesFuture, Duration.Inf)
    oldDonatables.forEach {
      case account: JsonObject =>
        val donater = DonatableWithoutId(
          account.getString("name"),
          account.getString("imageUrl"),
          BigDecimal(account.getDouble("amount")),
          BigDecimal(account.getDouble("balance"))
        )
        newClient.post("/api/donatables")
          .handler(_ => ())
          .putHeader("Content-Type", "application/json")
          .end(donater.asJson.noSpaces)
    }
  }

  private def createDonaters(oldFuture: Future[JsonArray], newClient: HttpClient) = {
    val oldAccounts = Await.result(oldFuture, Duration.Inf)
    oldAccounts.forEach {
      case account: JsonObject =>
        val donater = DonaterWithoutId(
          account.getString("name"),
          account.getString("email"),
          BigDecimal(account.getDouble("balance"))
        )
        newClient.post("/api/donaters")
          .handler(_ => ())
          .putHeader("Content-Type", "application/json")
          .end(donater.asJson.noSpaces)
    }
  }
}
