package ski.bedrit.stock.api

import akka.actor.ActorSystem
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import akka.testkit.TestKit
import org.specs2.mutable.SpecificationLike
import ski.bedrit.stock.api.JsonSupport._
import spray.json._

import scala.concurrent.Await
import scala.concurrent.duration._

class ApiSpec extends TestKit(ActorSystem()) with SpecificationLike {
  "Api" should {
    val api = new RestApi

    "correctly handle OK response" in {
      // Given
      implicit val materializer = ActorMaterializer()

      val url = "/foo/bar"
      val httpResponse = HttpResponse(StatusCodes.OK, Nil, HttpEntity(ContentTypes.`application/json`, ApiHeartbeatResponse(true, "").toJson.toString()))

      // When
      val response = Await.result(api.handleResponse[ApiHeartbeatResponse](url, httpResponse), 5 seconds)

      // Then
      response must beEqualTo(Right(ApiHeartbeatResponse(true, "")))
    }

    "correctly handle BadRequest response" in {
      // Given
      implicit val materializer = ActorMaterializer()

      val url = "/foo/bar"
      val httpResponse = HttpResponse(StatusCodes.BadRequest, Nil, HttpEntity(ContentTypes.`application/json`, ApiError(true, "").toJson.toString()))

      // When
      val response = Await.result(api.handleResponse[ApiHeartbeatResponse](url, httpResponse), 5 seconds)

      // Then
      response must beEqualTo(Left(ApiError(true, "")))
    }

    "throw exception on 404" in {
      // Given
      implicit val materializer = ActorMaterializer()

      val url = "/foo/bar"
      val httpResponse = HttpResponse(StatusCodes.NotFound)

      // When Then
      api.handleResponse[ApiHeartbeatResponse](url, httpResponse) must throwA[ApiException]
    }
  }
}
