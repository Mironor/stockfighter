package ski.bedrit.stock.api

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model.{RequestEntity, _}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.Materializer
import com.typesafe.config.ConfigFactory
import ski.bedrit.stock.api.JsonSupport._
import spray.json._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait Api {
  def checkApi()(implicit actorSystem: ActorSystem, materializer: Materializer): Future[(StatusCode, Seq[HttpHeader], ApiResponse)]

  def sendOrder(order: Order)(implicit actorSystem: ActorSystem, materializer: Materializer): Future[(StatusCode, Seq[HttpHeader], OrderResponse)]
}

class RestApi extends Api {

  val conf = ConfigFactory.load()
  val apiKey = conf.getString("api.key")
  val base_url = conf.getString("api.base_url")

  val defaultHeaders = List(RawHeader("X-Starfighter-Authorization", apiKey))

  /**
    * A simple health check for the API
    * @param actorSystem implicit actor system of the entity calling the method
    * @param materializer implicit actor materializer of the entity calling the method
    * @return Future[(StatusCode, Seq[HttpHeader], ApiResponse)] parser reply from the server wrapped in a Future
    */
  override def checkApi()(implicit actorSystem: ActorSystem, materializer: Materializer): Future[(StatusCode, Seq[HttpHeader], ApiResponse)] = {
    val url = s"$base_url/heartbeat"
    val request = HttpRequest(HttpMethods.GET, url, defaultHeaders)

    for {
      requestResult <- Http(actorSystem).singleRequest(request)
      apiResponse <- Unmarshal(requestResult.entity).to[ApiResponse]
    } yield (requestResult.status, requestResult.headers, apiResponse)
  }

  override def sendOrder(order: Order)(implicit actorSystem: ActorSystem, materializer: Materializer): Future[(StatusCode, Seq[HttpHeader], OrderResponse)] = {
    val url = s"$base_url/venues/${order.venue}/stocks/${order.symbol}/orders"

    for {
      orderJson <- Marshal(order.toJson).to[RequestEntity]
      request = HttpRequest(HttpMethods.POST, url, defaultHeaders, orderJson)
      requestResult <- Http(actorSystem).singleRequest(request)
      orderResponse <- Unmarshal(requestResult.entity).to[OrderResponse]
    } yield (requestResult.status, requestResult.headers, orderResponse)
  }

}
