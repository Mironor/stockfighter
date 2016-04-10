package ski.bedrit.stock.api

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model.{RequestEntity, _}
import akka.http.scaladsl.unmarshalling.{Unmarshal, Unmarshaller}
import akka.stream.Materializer
import com.typesafe.config.ConfigFactory
import ski.bedrit.stock.api.JsonSupport._
import spray.json._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait Api {
  def checkApi()(implicit actorSystem: ActorSystem, materializer: Materializer): Future[Either[ApiError, ApiHeartbeatResponse]]

  def sendOrderBook(orderBook: OrderBook)(implicit actorSystem: ActorSystem, materializer: Materializer): Future[Either[ApiError, OrderBookResponse]]

  def sendOrder(order: Order)(implicit actorSystem: ActorSystem, materializer: Materializer): Future[Either[ApiError, OrderResponse]]
}

case class ApiException(message: String) extends Exception(message)

class RestApi extends Api {

  val conf = ConfigFactory.load()
  val apiKey = conf.getString("api.key")
  val base_url = conf.getString("api.base_url")

  val defaultHeaders = List(RawHeader("X-Starfighter-Authorization", apiKey))

  /**
    * A simple health check for the API
    *
    * @param actorSystem  implicit actor system of the entity calling the method
    * @param materializer implicit actor materializer of the entity calling the method
    * @return Future[ Either[ApiError, ApiHeartbeatResponse] ] parser reply from the server wrapped in a Future
    */
  override def checkApi()(implicit actorSystem: ActorSystem, materializer: Materializer): Future[Either[ApiError, ApiHeartbeatResponse]] = {
    val url = s"$base_url/heartbeat"
    val request = HttpRequest(HttpMethods.GET, url, defaultHeaders)

    for {
      httpResponse <- Http(actorSystem).singleRequest(request)
      response <- handleResponse[ApiHeartbeatResponse](url, httpResponse)
    } yield response
  }

  /**
    * Handles `HttpResponse` by deserializing the result to the supplied type or to the `ApiError`
    *
    * @param url          request's api, used in the exception's message for debugging
    * @param httpResponse request's result
    * @param materializer implicit actor materializer of the entity calling the method
    * @param unmarshaller implicit unmarshaller to deserialize the entity
    * @tparam T the case class to which deserialize the entity in case the response is OK
    * @return `Either` with either the deserialized entity or the `ApiError`
    */
  def handleResponse[T](url: String, httpResponse: HttpResponse)(implicit materializer: Materializer, unmarshaller: Unmarshaller[ResponseEntity, T]): Future[Either[ApiError, T]] =
    (httpResponse match {
      case HttpResponse(StatusCodes.OK, headers, entity, _) => Unmarshal(entity).to[T].map(Right.apply)
      case HttpResponse(StatusCodes.BadRequest, headers, entity, _) => Unmarshal(entity).to[ApiError].map(Left.apply)
      case HttpResponse(StatusCodes.NotFound, _, _, _) => throw new ApiException(s"Api url $url does not exist")
      case HttpResponse(statusCode, _, _, _) => throw new ApiException(s"Unhandled status code $statusCode for url $url")
    }).recover {
      case DeserializationException(msg, cause, fieldNames) => throw new ApiException(s"Deserialization Exception: url: $url; msg: $msg; fields: $fieldNames")
      case default => throw new ApiException(s"Default exception case: url: $url, exception: $default")
    }

  override def sendOrderBook(orderBook: OrderBook)(implicit actorSystem: ActorSystem, materializer: Materializer): Future[Either[ApiError, OrderBookResponse]] = {
    val url = s"$base_url/venues/${orderBook.venue}/stocks/${orderBook.stock}"

    for {
      orderBookJson <- Marshal(orderBook.toJson).to[RequestEntity]
      request = HttpRequest(HttpMethods.POST, url, defaultHeaders, orderBookJson)
      httpResponse <- Http(actorSystem).singleRequest(request)
      response <- handleResponse[OrderBookResponse](url, httpResponse)
    } yield response
  }

  /**
    * Sends order to the stock market
    *
    * @param order        order to send
    * @param actorSystem  implicit actor system of the entity calling the method
    * @param materializer implicit actor materializer of the entity calling the method
    * @return Future[ Either[ApiError, OrderResponse] ] parser reply from the server wrapped in a Future
    */
  override def sendOrder(order: Order)(implicit actorSystem: ActorSystem, materializer: Materializer): Future[Either[ApiError, OrderResponse]] = {
    val url = s"$base_url/venues/${order.venue}/stocks/${order.stock}/orders"

    for {
      orderJson <- Marshal(order.toJson).to[RequestEntity]
      request = HttpRequest(HttpMethods.POST, url, defaultHeaders, orderJson)
      httpResponse <- Http(actorSystem).singleRequest(request)
      response <- handleResponse[OrderResponse](url, httpResponse)
    } yield response
  }

}
