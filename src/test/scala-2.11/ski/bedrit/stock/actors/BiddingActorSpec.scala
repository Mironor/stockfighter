package ski.bedrit.stock.actors

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import org.specs2.mutable.SpecificationLike
import ski.bedrit.stock.actors.BiddingActor.SendOrder
import ski.bedrit.stock.api.Order

class BiddingActorSpec extends TestKit(ActorSystem()) with ImplicitSender with SpecificationLike {
  "OrderingActor" should {
    "send order to the api" in {
      val actor = system.actorOf(Props[BiddingActor])
      val order = Order("account", "venue", "symbol", 250, 1, "direction", "type")

      actor ! SendOrder(order)
      true must beTrue
    }
  }
}
