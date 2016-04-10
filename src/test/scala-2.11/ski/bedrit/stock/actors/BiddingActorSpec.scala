package ski.bedrit.stock.actors

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestActorRef, TestKit, TestProbe}
import org.specs2.mutable.SpecificationLike
import ski.bedrit.stock.actors.BiddingActor.{SendOrder, SendOrderNok, SendOrderOk}
import ski.bedrit.stock.api._

class BiddingActorSpec extends TestKit(ActorSystem()) with ImplicitSender with SpecificationLike {

  val successfulApiMock = new SuccessfulApiMock
  val unsuccessfulApiMock = new UnsuccessfulApiMock

  // Constants
  val account = "account"
  val venue = "TESTEX"
  val symbol = "TEST"
  val buy = "buy"
  val limit = "limit"

  "OrderingActor" should {
    "successfully send order to the api" in {
      // Given
      val parent = TestProbe()
      val actor = TestActorRef[BiddingActor](Props(new BiddingActor(successfulApiMock)), parent.ref)
      val price = 25000
      val quantity = 100
      val order = Order(account, venue, symbol, price, quantity, buy, limit)

      // When
      parent.send(actor, SendOrder(order))

      // Then
      parent.expectMsg(SendOrderOk(
        OrderResponse(true, order.symbol, order.venue, order.direction, 1000, 1000 - order.qty, order.price,
          order.orderType, 0, order.account, "", List(Fill(5909, order.qty, "")), order.qty, false))
      )
      success
    }

    "unsuccessfully send order to the api" in {
      // Given
      val parent = TestProbe()
      val actor = TestActorRef[BiddingActor](Props(new BiddingActor(unsuccessfulApiMock)), parent.ref)
      val price = 25000
      val quantity = 100
      val order = Order(account, venue, symbol, price, quantity, buy, limit)

      // When
      parent.send(actor, SendOrder(order))

      // Then
      parent.expectMsg(SendOrderNok(true, ""))
      success
    }
  }
}
