package donatr

import java.util.UUID

import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.server.TwitterServer
import com.twitter.util.Await
import io.circe.generic.auto._
import io.finch._
import io.finch.circe._


object DonatrServer extends TwitterServer {
  def postDonater: Endpoint[Unit] = post("donaters" :: jsonBody[DonaterWithoutId]) { d: DonaterWithoutId =>
    DonatrCore.processCommand(CreateDonater(d)) match {
      case EventOrFailure(Some(DonaterCreated(Donater(id, _, _, _))), None) =>
        Output.unit(Status.Created).withHeader("Location" -> s"/donaters/$id")
      case EventOrFailure(None, Some(failure)) =>
        throw failure
    }
  }

  def postDonatable: Endpoint[Unit] = post("donatables" :: jsonBody[DonatableWithoutId]) { d: DonatableWithoutId =>
    DonatrCore.processCommand(CreateDonatable(d)) match {
      case EventOrFailure(Some(DonatableCreated(Donatable(id, _, _, _))), None) =>
        Output.unit(Status.Created).withHeader("Location" -> s"/donatables/$id")
      case EventOrFailure(None, Some(failure)) =>
        throw failure
    }
  }

  def postFundable: Endpoint[Unit] = post("fundables" :: jsonBody[FundableWithoutId]) { d: FundableWithoutId =>
    DonatrCore.processCommand(CreateFundable(d)) match {
      case EventOrFailure(Some(FundableCreated(Fundable(id, _, _, _))), None) =>
        Output.unit(Status.Created).withHeader("Location" -> s"/fundables/$id")
      case EventOrFailure(None, Some(failure)) =>
        throw failure
    }
  }

  def postDonation: Endpoint[Unit] = post("donations" :: jsonBody[DonationWithoutId]) { d: DonationWithoutId =>
    DonatrCore.processCommand(CreateDonation(d)) match {
      case EventOrFailure(Some(DonationCreated(Donation(id, _, _, _))), None) =>
        Output.unit(Status.Created).withHeader("Location" -> s"/donations/$id")
      case EventOrFailure(None, Some(failure)) =>
        throw failure
    }
  }

  def getDonater: Endpoint[Donater] = get("donaters" :: uuid) { id: UUID =>
    getEntity(id, DonatrCore.state.donaters)
  }

  def getDonatable: Endpoint[Donatable] = get("donatables" :: uuid) { id: UUID =>
    getEntity(id, DonatrCore.state.donatables)
  }

  def getFundable: Endpoint[Fundable] = get("fundables" :: uuid) { id: UUID =>
    getEntity(id, DonatrCore.state.fundables)
  }

  def getDonation: Endpoint[Donation] = get("donations" :: uuid) { id: UUID =>
    getEntity(id, DonatrCore.state.donations)
  }

  def getEntity[T](id: UUID, map: Map[UUID, T]): Output[T] = {
    if (map.contains(id)) {
      Ok(map(id))
    } else {
      throw EntityNotFound()
    }
  }

  import com.twitter.finagle.{Http, Service}

  val api: Service[Request, Response] = (
    postDonater :+: getDonater
      :+: postDonatable :+: getDonatable
      :+: postFundable :+: getFundable
      :+: postDonation :+: getDonation
    ).handle({
    case e: EntityNotFound => NotFound(e)
    case e: UnknownCommand => BadRequest(e)
    case e: NameTaken => BadRequest(e)
    case e: NoSuchElementException => NotFound(e)
    case e: Exception => InternalServerError(e)
  }).toServiceAs[Application.Json]

  def main(): Unit = {
    import scala.concurrent.ExecutionContext
    import ExecutionContext.Implicits.global
    DonatrCore.rebuildState
    val server = Http.server
      .serve(":8080", api)

    onExit {
      server.close()
    }

    Await.ready(adminHttpServer)
  }
}

