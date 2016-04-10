package ski.bedrit.stock.api

import akka.actor.ActorSystem
import akka.stream.Materializer

import scala.concurrent.Future

class UnsuccessfulApiMock extends Api{
  val defaultResponse = Future.successful(Left(ApiError(true, "")))

  override def checkApi()(implicit actorSystem: ActorSystem, materializer: Materializer) = defaultResponse

  override def sendOrder(order: Order)(implicit actorSystem: ActorSystem, materializer: Materializer) = defaultResponse

  override def sendOrderBook(orderBook: OrderBook)(implicit actorSystem: ActorSystem, materializer: Materializer) = defaultResponse
}
