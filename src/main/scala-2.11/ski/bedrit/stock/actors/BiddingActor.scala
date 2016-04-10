package ski.bedrit.stock.actors

import akka.actor.{Actor, ActorLogging}
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import ski.bedrit.stock.actors.BiddingActor.{SendOrder, SendOrderNok, SendOrderOk, SendOrderResponse}
import ski.bedrit.stock.api.{Api, ApiError, Order, OrderResponse}

object BiddingActor {

  case class SendOrder(order: Order)

  case class SendOrderResponse(response: Either[ApiError, OrderResponse])

  case class SendOrderOk(orderResponse: OrderResponse)

  case class SendOrderNok(ok: Boolean, error: String)

}

class BiddingActor(val api: Api) extends Actor with ActorLogging {

  import akka.pattern.pipe
  import context.dispatcher

  implicit val system = context.system
  implicit val materializer = ActorMaterializer(ActorMaterializerSettings(system))

  def receive = {
    case SendOrder(order) => api.sendOrder(order).map(SendOrderResponse.apply).pipeTo(self)

    case SendOrderResponse(Right(response)) => context.parent ! SendOrderOk(response)

    case SendOrderResponse(Left(ApiError(ok, error))) => context.parent ! SendOrderNok(ok, error)
  }
}


