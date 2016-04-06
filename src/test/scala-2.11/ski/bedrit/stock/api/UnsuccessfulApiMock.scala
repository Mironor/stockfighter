package ski.bedrit.stock.api

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes
import akka.stream.Materializer

import scala.concurrent.Future

class UnsuccessfulApiMock extends Api{
  override def checkApi()(implicit actorSystem: ActorSystem, materializer: Materializer) = Future.successful((StatusCodes.InternalServerError, Nil, ApiResponse(true, "")))

  override def sendOrder(order: Order)(implicit actorSystem: ActorSystem, materializer: Materializer) = ???
}
