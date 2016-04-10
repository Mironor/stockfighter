package ski.bedrit.stock.actors

import akka.actor.{Actor, ActorLogging}
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import ski.bedrit.stock.actors.InformationActor.{FetchOrderBook, FetchOrderBookResponse, OrderBookNok, OrderBookOk}
import ski.bedrit.stock.api._

object InformationActor{
  case class FetchOrderBook(venue: String, stock: String)

  case class FetchOrderBookResponse(response: Either[ApiError, OrderBookResponse])

  case class OrderBookOk(orderBookResponse: OrderBookResponse)

  case class OrderBookNok(ok: Boolean, error: String)
}

class InformationActor(val api: Api) extends Actor with ActorLogging {

  import akka.pattern.pipe
  import context.dispatcher

  implicit val system = context.system
  implicit val materializer = ActorMaterializer(ActorMaterializerSettings(system))

  override def receive = {
    case FetchOrderBook(venue, stock) =>
      log.info(s"Going to fetch $venue: $stock")
      api.sendOrderBook(venue, stock).map(FetchOrderBookResponse.apply).pipeTo(self).recover{
        case ApiException(msg) => log.error(msg)
      }

    case FetchOrderBookResponse(Right(orderBookResponse)) =>
      log.info(s"Received orderbook response: $orderBookResponse")
      context.parent ! OrderBookOk(orderBookResponse)

    case FetchOrderBookResponse(Left(ApiError(ok, error))) =>
      log.info(s"Received ERROR from orderbook: error")
      context.parent ! OrderBookNok(ok, error)

  }

}
