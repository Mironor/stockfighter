package ski.bedrit.stock.actors

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestActorRef, TestKit, TestProbe}
import org.specs2.mutable.SpecificationLike
import ski.bedrit.stock.actors.InformationActor.{FetchOrderBook, OrderBookNok, OrderBookOk}
import ski.bedrit.stock.api._

class InformationActorSpec extends TestKit(ActorSystem()) with ImplicitSender with SpecificationLike {

  val successfulApiMock = new SuccessfulApiMock
  val unsuccessfulApiMock = new UnsuccessfulApiMock

  // Constants
  val account = "account"
  val venue = "TESTEX"
  val stock = "TEST"

  "InformationActor" should {
    "successfully send order to the api" in {
      // Given
      val parent = TestProbe()
      val actor = TestActorRef[InformationActor](Props(new InformationActor(successfulApiMock)), parent.ref)

      // When
      parent.send(actor, FetchOrderBook(venue, stock))

      // Then
      parent.expectMsg(OrderBookOk(OrderBookResponse(true, venue, stock, None, None, "")))
      success
    }

    "unsuccessfully send order to the api" in {
      // Given
      val parent = TestProbe()
      val actor = TestActorRef[InformationActor](Props(new InformationActor(unsuccessfulApiMock)), parent.ref)

      // When
      parent.send(actor, FetchOrderBook(venue, stock))

      // Then
      parent.expectMsg(OrderBookNok(true, ""))
      success
    }
  }
}
