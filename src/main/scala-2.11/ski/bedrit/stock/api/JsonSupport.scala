package ski.bedrit.stock.api

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

case class ApiResponse(ok: Boolean, error: String)

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
                         open: Boolean)

case class Fill(price: Int, qty: Int, ts: String)

object JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val checkApiResponseFormat = jsonFormat2(ApiResponse)
  implicit val OrderFormat = jsonFormat7(Order)
  implicit val FillFormat = jsonFormat3(Fill)
  implicit val OrderResponseFormat = jsonFormat14(OrderResponse)
}


