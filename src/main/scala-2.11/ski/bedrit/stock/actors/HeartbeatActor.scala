package ski.bedrit.stock.actors

import akka.actor.{Actor, ActorLogging}
import akka.http.scaladsl.model._
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import ski.bedrit.stock.actors.HeartbeatActor.{ApiNok, ApiOk, CheckApi, CheckApiResponse}
import ski.bedrit.stock.api.{Api, ApiResponse}
import spray.json.DeserializationException

object HeartbeatActor {

  case object CheckApi

  case class CheckApiResponse(response: (StatusCode, Seq[HttpHeader], ApiResponse))

  case object ApiOk

  case class ApiNok(code: Int, ok: Boolean, error: String)

  case class CheckVenue(venue: String)
}

/**
  * Actor responsible for api:
  * Check The API Is Up
  * Check The Venue is Up
  * @param api service making api calls
  */
class HeartbeatActor(api: Api) extends Actor with ActorLogging {

  import akka.pattern.pipe
  import context.dispatcher

  implicit val system = context.system
  implicit val materializer = ActorMaterializer(ActorMaterializerSettings(system))

  def receive = {
    // A simple health check for API
    case CheckApi =>
      api.checkApi().map(CheckApiResponse.apply).pipeTo(self)
        .recover {
          case DeserializationException(msg, cause, fieldNames) => log.error(s"Deserialization Exception: $msg. Fields: $fieldNames")
          case default => log.error(s"Default exception case: $default")
        }

    case CheckApiResponse((StatusCodes.OK, _, ApiResponse(true, _))) =>
      context.parent ! ApiOk

    case CheckApiResponse((status, _, ApiResponse(ok, error))) =>
      context.parent ! ApiNok(status.intValue(), ok, error)
  }
}


