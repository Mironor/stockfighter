package ski.bedrit.stock.actors

import akka.actor.{Actor, ActorLogging}
import akka.http.scaladsl.model._
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import ski.bedrit.stock.actors.BiddingActor.{SendOrder, SendOrderResponse}
import ski.bedrit.stock.api.{Api, Order, OrderResponse}

object BiddingActor {

  case class SendOrder(order: Order)

  case class SendOrderResponse(response: (StatusCode, Seq[HttpHeader], OrderResponse))

  case class OrderOk(order: Order)

}

class BiddingActor(val api: Api) extends Actor with ActorLogging {

  import akka.pattern.pipe
  import context.dispatcher

  implicit val system = context.system
  implicit val materializer = ActorMaterializer(ActorMaterializerSettings(system))

  def receive = {
    case SendOrder(order) => api.sendOrder(order).map(SendOrderResponse.apply).pipeTo(self).recover{
      case x => println(x)
    }

    case SendOrderResponse((statusCode, _, response)) =>
      log.info(s"Got response! Code $statusCode")
      log.info(s"Content: $response")
      context.system.terminate()

    case HttpResponse(code, _, entity, _) =>
      log.info("Request failed!")
      log.info("Response code: " + code)
      log.info("Content" + entity)
      context.system.terminate()
  }
}


