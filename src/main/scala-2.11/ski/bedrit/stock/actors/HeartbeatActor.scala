package ski.bedrit.stock.actors

import akka.actor.{Actor, ActorLogging}
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import ski.bedrit.stock.actors.HeartbeatActor.{ApiNok, ApiOk, CheckApi, CheckApiResponse}
import ski.bedrit.stock.api.{Api, ApiError, ApiHeartbeatResponse}

object HeartbeatActor {

  case object CheckApi

  case class CheckApiResponse(response: Either[ApiError, ApiHeartbeatResponse])

  case object ApiOk

  case class ApiNok(ok: Boolean, error: String)

  case class CheckVenue(venue: String)
}

/**
  * Actor responsible for api:
  * Check The API Is Up
  * Check The Venue is Up
  *
  * @param api service making api calls
  */
class HeartbeatActor(api: Api) extends Actor with ActorLogging {

  import akka.pattern.pipe
  import context.dispatcher

  implicit val system = context.system
  implicit val materializer = ActorMaterializer(ActorMaterializerSettings(system))

  def receive = {
    // A simple health check for API
    case CheckApi => api.checkApi().map(CheckApiResponse.apply).pipeTo(self)

    case CheckApiResponse(Right(ApiHeartbeatResponse(true, _))) =>
      context.parent ! ApiOk

    case CheckApiResponse(Right(ApiHeartbeatResponse(false, error))) =>
      context.parent ! ApiNok(false, error)

    case CheckApiResponse(Left(ApiError(ok, error))) =>
      context.parent ! ApiNok(ok, error)
  }
}


