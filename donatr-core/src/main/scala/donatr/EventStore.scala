package donatr

import java.util.UUID

import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import org.slf4j.LoggerFactory
import slick.jdbc.H2Profile.api._
import slick.lifted.{ProvenShape, TableQuery}

class EventStore {

  import scala.concurrent.ExecutionContext
  import scala.concurrent.duration.Duration

  private val log = LoggerFactory.getLogger("EventStore")

  private val db: _root_.slick.jdbc.H2Profile.backend.DatabaseDef =
    Database.forURL("jdbc:h2:file:/Users/derveloper/Projects/scala/donatr4/donatr.h2.db", driver = "org.h2.Driver")

  private class Events(tag: Tag) extends Table[(String, String)](tag, "EVENTS") {
    def id: Rep[String] = column[String]("ID", O.PrimaryKey)

    def payload: Rep[String] = column[String]("PAYLOAD")

    def * : ProvenShape[(String, String)] =
      (id, payload)
  }

  private val events: TableQuery[Events] = TableQuery[Events]
  try {
    scala.concurrent.Await.result(db.run(DBIO.seq(
      // create the schema
      events.schema.create
    )), Duration.Inf)
  }
  catch {
    case e: Throwable => log.info(e.getLocalizedMessage)
  }

  def insert(event: Event): Unit = {
    scala.concurrent.Await.result(db.run(DBIO.seq(
      events += (UUID.randomUUID().toString, event.asJson.noSpaces)
    )), Duration.Inf)
  }

  def getEvents(implicit ec: ExecutionContext): Seq[Event] = {
    scala.concurrent.Await.result(db.run(events.result).map { allEvents =>
      allEvents.map { e =>
        decode[Event](e._2)
      }
    }, Duration.Inf).map { e =>
      e.right.get
    }
  }
}
