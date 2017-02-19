package donatr

import java.util.UUID

import org.http4s.headers.`Location`
import org.http4s.server.blaze.BlazeBuilder
import org.http4s.server.websocket.WS
import org.http4s.server.{Server, ServerApp}
import org.http4s.websocket.WebsocketBits.Text
import org.slf4j.{Logger, LoggerFactory}

import scalaz.concurrent.Task
import scalaz.stream.{Exchange, Process}

trait Logging {
  protected lazy val log: Logger = LoggerFactory.getLogger(this.getClass)
}

case class ErrorResponse(message: String)

case class DonaterPatch(name: Option[String],
                        email: Option[String])

object DonatrHttp4sServer extends ServerApp with Logging {

  import java.util.concurrent.Executors

  import io.circe.generic.auto._
  import io.circe.syntax._
  import org.http4s._
  import org.http4s.circe._
  import org.http4s.dsl._

  import scalaz.stream.async.topic

  private implicit val scheduledEC = Executors.newScheduledThreadPool(4)

  private val eventTopic = topic[Event]()

  class EP extends EventPublisher {
    override def publish(event: Event): Unit = {
      log.info(event.toString)
      eventTopic.publishOne(event).unsafePerformSync
    }
  }

  private implicit val ep = new EP()
  private implicit val es = new EventStore()
  val donatr = new DonatrCore()

  import donatr._

  private def static(file: String, request: Request) =
    StaticFile.fromResource("/webroot/" + file, Some(request)).map(Task.now).getOrElse(NotFound())

  val fileService = HttpService {
    case request@GET -> Root => static("index.html", request)
    case request@GET -> Root / path if List(".js", ".css", ".map", ".html", ".webm").exists(path.endsWith) =>
      static(path, request)
    case request@GET -> _ =>
      static("index.html", request)
  }

  val donatrService = HttpService {
    case GET -> Root / "donaters" => Ok(donatr.donaters.values.asJson)
    case GET -> Root / "donaters" / id => Ok(donatr.donaters(UUID.fromString(id)).asJson)
    case GET -> Root / "donatables" => Ok(donatr.donatables.values.asJson)
    case GET -> Root / "fundables" => Ok(donatr.fundables.values.asJson)

    case r@POST -> Root / "donaters" =>
      r.as(jsonOf[DonaterWithoutId]).flatMap { donater =>
        CreatedResponseFrom("/api/donaters", donatr.create(donater))
      }

    case r@POST -> Root / "donatables" =>
      r.as(jsonOf[DonatableWithoutId]).flatMap { donatable =>
        CreatedResponseFrom("/api/donatables", donatr.create(donatable))
      }

    case r@POST -> Root / "fundables" =>
      r.as(jsonOf[FundableWithoutId]).flatMap { fundable =>
        CreatedResponseFrom("/api/fundables", donatr.create(fundable))
      }

    case r@POST -> Root / "donations" =>
      r.as(jsonOf[DonationWithoutId]).flatMap { donation =>
        CreatedResponseFrom("/api/donations", donatr.create(donation))
      }.or(r.as(jsonOf[DonationWithoutIdAndFrom]).flatMap(f =>
        CreatedResponseFrom("/api/donations", donatr.create(f))))

    case r@PUT -> Root / "donaters" / id =>
      r.as(jsonOf[DonaterPatch]).flatMap { donater =>
        donater.name.map(name => donatr.changeName(state.donaters(UUID.fromString(id)), name))
        NoContent()
      }

    case r@GET -> Root / "events" =>
      val src = eventTopic.subscribe.map(e => Text(e.asJson.noSpaces))
      WS(Exchange(src, Process.halt))
  }

  private def CreatedResponseFrom(path: String, res: Either[Throwable, UUID]) = {
    res.fold(e => BadRequest(ErrorResponse(e.getMessage).asJson),
      d => Created().putHeaders(`Location`(Uri.unsafeFromString(s"$path/$d"))))
  }

  override def server(args: List[String]): Task[Server] = {
    val port = Option(System.getenv("PORT")).map(_.toInt).getOrElse(8080)
    BlazeBuilder
      .bindHttp(port, "0.0.0.0")
      .mountService(fileService, "/")
      .mountService(donatrService, "/api")
      .start
  }
}
