package ski.bedrit.stock.api

import akka.actor.ActorSystem
import akka.stream.Materializer

import scala.concurrent.Future

class UnsuccessfulApiMock extends Api{
  override def checkApi()(implicit actorSystem: ActorSystem, materializer: Materializer) = Future.successful(Left(ApiError(true, "")))

  override def sendOrder(order: Order)(implicit actorSystem: ActorSystem, materializer: Materializer) = Future.successful(Left(ApiError(true, "")))
}
