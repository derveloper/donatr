package donatr

import java.util.UUID

import io.vertx.scala.core.Vertx
import io.vertx.scala.core.http.ServerWebSocket
import io.vertx.scala.ext.web.handler.BodyHandler

object DonatrVertxServer {

  import io.circe.generic.auto._
  import io.circe.parser._
  import io.circe.syntax._
  import io.vertx.scala.ext.web.{Router, RoutingContext}

  val vertx: Vertx = Vertx.vertx()

  class VertxEventPublisher extends EventPublisher {
    override def publish(event: Event): Unit = {
      vertx.eventBus().publish("event", event.asJson.noSpaces)
    }
  }

  implicit val ep = new VertxEventPublisher()
  val donatr = new DonatrCore(ledger = Ledger(UUID.randomUUID()))

  private def websocketHandler(socket: ServerWebSocket) = {
    vertx.eventBus().consumer[String]("event")
      .handler(msg => socket.writeFinalTextFrame(msg.body()))
  }

  def main(args: Array[String]): Unit = {
    val router = Router.router(vertx)

    router.route().handler(BodyHandler.create())

    router.get("/api/donaters").handler { ctx =>
      ok(ctx, donatr.state.donaters.map(_._2.asJson).asJson.noSpaces)
    }

    router.get("/api/donatables").handler { ctx =>
      ok(ctx, donatr.state.donatables.map(_._2.asJson).asJson.noSpaces)
    }

    router.get("/api/fundables").handler { ctx =>
      ok(ctx, donatr.state.fundables.map(_._2.asJson).asJson.noSpaces)
    }

    router.post("/api/donaters").handler(postDonater)
    router.post("/api/donatables").handler(postDonatable)
    router.post("/api/fundables").handler(postFundable)
    router.post("/api/donations").handler(postDonation)

    vertx
      .createHttpServer()
      .websocketHandler(websocketHandler)
      .requestHandler(router.accept)
      .listenFuture(8080)
  }

  private def postDonater(ctx: RoutingContext) = {
    decode[DonaterWithoutId](ctx.getBodyAsString.getOrElse(""))
      .flatMap(d => donatr.processCommand(CreateDonater(d)))
      .fold(badRequest(ctx, _),
        event => created(ctx, s"/api/donaters/${event.donater.id}"))
  }

  private def postDonatable(ctx: RoutingContext) = {
    decode[DonatableWithoutId](ctx.getBodyAsString.getOrElse(""))
      .flatMap(d => donatr.processCommand(CreateDonatable(d)).map(_.donatable))
      .fold(badRequest(ctx, _), d => created(ctx, s"/api/donatables/${d.id}"))
  }

  private def postFundable(ctx: RoutingContext) = {
    decode[FundableWithoutId](ctx.getBodyAsString.getOrElse(""))
      .flatMap(d => donatr.processCommand(CreateFundable(d)).map(_.fundable))
      .fold(badRequest(ctx, _), d => created(ctx, s"/api/fundables/${d.id}"))
  }

  private def postDonation(ctx: RoutingContext) = {
    decode[DonationWithoutId](ctx.getBodyAsString.getOrElse(""))
      .flatMap(d => donatr.processCommand(CreateDonation(d)))
      .map(_.donation)
      .fold(badRequest(ctx, _), d => created(ctx, s"/api/donations/${d.id}"))
  }

  private def ok(ctx: RoutingContext, json: String) = {
    ctx.response().putHeader("Content-Type", "application/json").end(json)
  }

  private def badRequest(ctx: RoutingContext, err: Throwable) = {
    ctx.response().setStatusCode(400).end(("message" -> err.getMessage).asJson.noSpaces)
  }

  private def created(ctx: RoutingContext, path: String) = {
    ctx.response().setStatusCode(201).putHeader("Location", path).end()
  }
}
