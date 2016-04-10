package ski.bedrit.stock.api
import akka.actor.ActorSystem
import akka.stream.Materializer

import scala.concurrent.Future

class SuccessfulApiMock extends Api{
  override def checkApi()(implicit actorSystem: ActorSystem, materializer: Materializer) = Future.successful(Right(ApiHeartbeatResponse(true, "")))

  override def sendOrder(order: Order)(implicit actorSystem: ActorSystem, materializer: Materializer) = Future.successful{
    val response = OrderResponse(true, order.symbol, order.venue, order.direction, 1000,  1000 - order.qty,
      order.price, order.orderType, 0, order.account, "", List(Fill(5909, order.qty, "")), order.qty, false)

    Right(response)
  }
}
