package ski.bedrit.stock.api

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

case class ApiError(ok: Boolean, error: String)

case class ApiHeartbeatResponse(ok: Boolean, error: String)

case class OrderBookResponse(ok: Boolean, venue: String, symbol: String, bids: Option[List[Bid]], asks: Option[List[Ask]], ts: String)

case class Bid(price: Int, qty: Int, isBuy: Boolean)

case class Ask(price: Int, qty: Int, isBuy: Boolean)

case class Order(account: String, venue: String, stock: String, price: Int, qty: Int, direction: String, orderType: String)

case class OrderResponse(ok: Boolean, symbol: String, venue: String, direction: String, originalQty: Int, qty: Int, price: Int,
                         orderType: String, id: Int, account: String, ts: String, fills: List[Fill], totalFilled: Int, open: Boolean)

case class Fill(price: Int, qty: Int, ts: String)

object JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val apiErrorFormat = jsonFormat2(ApiError)
  implicit val HeartbeatApiFormat = jsonFormat2(ApiHeartbeatResponse)

  implicit val BidFormat = jsonFormat3(Bid)
  implicit val AskFormat = jsonFormat3(Ask)
  implicit val OrderBookResponseFormat = jsonFormat6(OrderBookResponse)

  implicit val OrderFormat = jsonFormat7(Order)
  implicit val FillFormat = jsonFormat3(Fill)
  implicit val OrderResponseFormat = jsonFormat14(OrderResponse)
}


