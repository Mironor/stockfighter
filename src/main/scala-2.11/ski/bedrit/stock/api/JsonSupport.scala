package ski.bedrit.stock.api

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

sealed trait ApiResponse

case class ApiError(ok: Boolean, error: String)

case class ApiHeartbeatResponse(ok: Boolean, error: String) extends ApiResponse

case class Order(account: String, venue: String, symbol: String, price: Int, qty: Int, direction: String, orderType: String)

case class OrderResponse(ok: Boolean,
                         symbol: String,
                         venue: String,
                         direction: String,
                         originalQty: Int,
                         qty: Int,
                         price: Int,
                         orderType: String,
                         id: Int,
                         account: String,
                         ts: String,
                         fills: List[Fill],
                         totalFilled: Int,
                         open: Boolean) extends ApiResponse

case class Fill(price: Int, qty: Int, ts: String)

object JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val apiErrorFormat = jsonFormat2(ApiError)
  implicit val HeartbeatApiFormat = jsonFormat2(ApiHeartbeatResponse)
  implicit val OrderFormat = jsonFormat7(Order)
  implicit val FillFormat = jsonFormat3(Fill)
  implicit val OrderResponseFormat = jsonFormat14(OrderResponse)
}


