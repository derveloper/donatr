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

  def getDonater: Endpoint[Donater] = get("donaters" :: uuid) { id: UUID =>
    DonatrCore.state.donaters.get(id) match {
      case Some(Donater(`id`, `name`, email, balance)) =>
        Ok(Donater(id, name, email, balance))
      case _ =>
        throw DonatrNotFound()
    }
  }

  def getDonatable: Endpoint[Donatable] = get("donatables" :: uuid) { id: UUID =>
    DonatrCore.state.donatables.get(id) match {
      case Some(Donatable(`id`, `name`, minDonationAmount, balance)) =>
        Ok(Donatable(id, name, minDonationAmount, balance))
      case _ =>
        throw DonatrNotFound()
    }
  }

  def getFundable: Endpoint[Fundable] = get("fundables" :: uuid) { id: UUID =>
    DonatrCore.state.fundables.get(id) match {
      case Some(Fundable(`id`, `name`, fundingTarget, balance)) =>
        Ok(Fundable(id, name, fundingTarget, balance))
      case _ =>
        throw DonatrNotFound()
    }
  }

  import com.twitter.finagle.{Http, Service}

  val api: Service[Request, Response] = (
    postDonater :+: getDonater
      :+: postDonatable :+: getDonatable
      :+: postFundable :+: getFundable
    ).handle({
    case e: DonatrNotFound => NotFound(e)
    case e: UnknownCommand => BadRequest(e)
    case e: NameTaken => BadRequest(e)
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

