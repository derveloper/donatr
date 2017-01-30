package donatr

import java.util.UUID

import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.finagle.{Http, Service}
import com.twitter.server.TwitterServer
import com.twitter.util.Await
import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.parser._
import io.finch._
import io.finch.circe._
import slick.driver.H2Driver.api._
import slick.lifted.{ProvenShape, TableQuery}

import scala.collection.immutable.List
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.Duration

sealed trait BaseDonatable {
  val id: Option[UUID]
  val name: String
  val balance: BigDecimal
}

case class Donatable(
                      id: Option[UUID],
                      name: String,
                      balance: BigDecimal = 0
                    ) extends BaseDonatable

case class FixedValueDonatable(
                                id: Option[UUID],
                                name: String,
                                value: BigDecimal,
                                balance: BigDecimal = 0
                              ) extends BaseDonatable

case class Transaction(id: Option[UUID], from: Donatable, to: Donatable, value: BigDecimal)

sealed trait Command

case class CreateDonatable(donatable: Donatable) extends Command

sealed trait Event

case class DonatableCreated(donatable: Donatable) extends Event

case class DonatableNameTaken() extends Exception {
  override def getMessage: String = s"Name is already taken."
}

case class UnknownCommand(cmd: Command) extends Exception {
  override def getMessage: String = s"Unknown command $cmd."
}

case class EventOrFailure(event: Option[Event] = None, failureMessage: Option[Exception] = None)

class EventStore {
  private val db: _root_.slick.driver.H2Driver.backend.DatabaseDef =
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
  catch { case e: Throwable => println(e.getLocalizedMessage) }

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

case class DonatrState(
                        donatables: Seq[BaseDonatable] = List.empty,
                        transactions: Seq[Transaction] = List.empty
                      ) {

  def handleCreate(event: DonatableCreated): DonatrState = {
    donatables.count(d => d.name == event.donatable.name) match {
      case 0 =>
        copy(donatables :+ event.donatable)
      case _ =>
        this
    }
  }

  def apply(event: Event): DonatrState = event match {
    case DonatableCreated(Donatable(id, name, balance)) =>
      handleCreate(DonatableCreated(Donatable(id, name, balance)))
    case _ => this
  }
}

object Donatr extends TwitterServer {
  val eventStore = new EventStore()
  var state = DonatrState()

  def rebuildState(implicit ec: ExecutionContext): Unit = {
    eventStore.getEvents.foreach { e =>
      state = state.apply(e)
    }
  }

  def processCommand(command: Command): EventOrFailure = {
    val result = command match {
      case CreateDonatable(Donatable(_, newName, balance)) => handleCreate(newName, balance)
      case _ => EventOrFailure(None, Some(UnknownCommand(command)))
    }
    result match {
      case EventOrFailure(Some(event), None) =>
        eventStore.insert(event)
        result
      case _ => result
    }
  }

  private def handleCreate(name: String, balance: BigDecimal) = {
    state.donatables.count(d => d.name == name) match {
      case 0 =>
        val newId = UUID.randomUUID()
        val created = DonatableCreated(Donatable(Some(newId), name, balance))
        state = state.apply(created)
        EventOrFailure(Some(created))
      case _ => EventOrFailure(None, Some(DonatableNameTaken()))
    }
  }

  def postDonatable: Endpoint[Unit] = post("donatables" :: jsonBody[Donatable]) { d: Donatable =>
    processCommand(CreateDonatable(d)) match {
      case EventOrFailure(Some(DonatableCreated(Donatable(Some(id), _, _))), None) =>
        Output.unit(Status.Created).withHeader("Location" -> s"/donatables/$id")
      case EventOrFailure(None, Some(failure)) =>
        throw failure
    }
  }

  def getDonatable: Endpoint[Donatable] = get("donatables" :: uuid) { id: UUID =>
    val filtered = state.donatables.filter(d => d.id.get == id)
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
    rebuildState
    val server = Http.server
      .serve(":8080", api)

    onExit { server.close() }

    Await.ready(adminHttpServer)
  }
}

