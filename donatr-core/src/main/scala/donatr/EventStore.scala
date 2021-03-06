package donatr

import java.util.UUID

import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import org.slf4j.LoggerFactory
import slick.jdbc.H2Profile.api._
import slick.lifted.{ProvenShape, TableQuery}

import scala.concurrent.Await.result

class EventStore(url: String = "jdbc:h2:file:./db/donatr.h2.db") {

  import scala.concurrent.ExecutionContext
  import scala.concurrent.duration.Duration

  private val log = LoggerFactory.getLogger("EventStore")
  private val db = Database.forURL(url = url, driver = "org.h2.Driver")

  private val events: TableQuery[Events] = TableQuery[Events]

  try {
    log.debug("creating schema")
    result(db.run(DBIO.seq(
      events.schema.create
    )), Duration.Inf)
  }
  catch {
    case e: Throwable => log.error(e.getMessage)
  }

  private class Events(tag: Tag) extends Table[(String, String)](tag, "EVENTS") {
    def id: Rep[String] = column[String]("ID", O.PrimaryKey)

    def payload: Rep[String] = column[String]("PAYLOAD")

    def * : ProvenShape[(String, String)] =
      (id, payload)
  }

  def insert(event: Event): Either[Throwable, Unit] = {
    try {
      result(db.run(DBIO.seq(
        events += ((UUID.randomUUID().toString, event.asJson.noSpaces))
      )), Duration.Inf)
      Right(())
    }
    catch {
      case e: Throwable => Left(e)
    }
  }

  def getEvents(implicit ec: ExecutionContext): Seq[Event] = {
    try {
      result(db.run(events.result).map { allEvents =>
        allEvents.map { e =>
          decode[Event](e._2)
        }
      }, Duration.Inf).map { e =>
        if(e.isLeft) {
          log.error("error fetching event", e.left.get)
          None
        }
        else {
          Some(e.right.get)
        }
      }.filter { e =>
        e.isDefined
      }.map { e => e.get }
    }
    catch {
      case e: Throwable =>
        log.error(e.getMessage, e)
        List.empty
    }
  }
}
