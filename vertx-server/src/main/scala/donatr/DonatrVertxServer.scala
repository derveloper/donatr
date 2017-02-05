package donatr

import io.vertx.scala.core.Vertx
import io.vertx.scala.ext.web.handler.BodyHandler

object DonatrVertxServer {
  import io.vertx.scala.ext.web.{Router, RoutingContext}
  import io.circe.generic.auto._
  import io.circe.parser._
  import io.circe.syntax._

  def main(args: Array[String]): Unit = {
    val vertx = Vertx.vertx()
    val router = Router.router(vertx)

    router.route().handler(BodyHandler.create())

    router.get("/api/donaters").handler { ctx =>
      val json = DonatrCore.state.donaters.map(_._2.asJson).asJson.noSpaces
      ctx.response().end(json)
    }

    router.post("/api/donaters").handler { ctx =>
      decode[DonaterWithoutId](ctx.getBodyAsString.getOrElse(""))
        .flatMap(d => DonatrCore.processCommand(CreateDonater(d)))
        .fold(badRequest(ctx, _),
          event => created(ctx, s"/api/donaters/${event.donater.id}"))
    }

    vertx
      .createHttpServer()
      .requestHandler(router.accept)
      .listenFuture(8080)
  }

  private def badRequest(ctx: RoutingContext, err: Exception) = {
    ctx.response().setStatusCode(400).end(("message" -> err.getMessage).asJson.noSpaces)
  }

  private def created(ctx: RoutingContext, path: String) = {
    ctx.response().setStatusCode(201).putHeader("Location", path).end()
  }
}
