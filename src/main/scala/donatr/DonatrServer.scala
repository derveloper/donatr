package donatr

import java.util.UUID

import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.finagle.{Http, Service}
import com.twitter.server.TwitterServer
import com.twitter.util.Await
import io.circe.generic.auto._
import io.finch._
import io.finch.circe._

import scala.concurrent.ExecutionContext


object DonatrServer extends TwitterServer {
  def postDonatable: Endpoint[Unit] = post("donatables" :: jsonBody[Donatable]) { d: Donatable =>
    Donatr.processCommand(CreateDonatable(d)) match {
      case EventOrFailure(Some(DonatableCreated(Donatable(Some(id), _, _))), None) =>
        Output.unit(Status.Created).withHeader("Location" -> s"/donatables/$id")
      case EventOrFailure(None, Some(failure)) =>
        throw failure
    }
  }

  def getDonatable: Endpoint[Donatable] = get("donatables" :: uuid) { id: UUID =>
    val filtered = Donatr.state.donatables.filter(d => d.id.get == id)
    filtered.length match {
      case 1 =>
        filtered.head match {
          case d: Donatable =>
            Ok(d)
          case _ =>
            NotFound(new RuntimeException())
        }
      case _ => NotFound(new RuntimeException())
    }
  }

  val api: Service[Request, Response] = (
      postDonatable :+: getDonatable
    ).handle({
      case e: UnknownCommand => BadRequest(e)
      case e: DonatableNameTaken => BadRequest(e)
    }).toServiceAs[Application.Json]

  def main(): Unit = {
    import ExecutionContext.Implicits.global
    Donatr.rebuildState
    val server = Http.server
      .serve(":8080", api)

    onExit { server.close() }

    Await.ready(adminHttpServer)
  }
}

