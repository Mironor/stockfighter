package ski.bedrit.stock.actors

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestActorRef, TestKit, TestProbe}
import org.specs2.mutable.SpecificationLike
import ski.bedrit.stock.actors.HeartbeatActor.{ApiNok, ApiOk, CheckApi}
import ski.bedrit.stock.api.{SuccessfulApiMock, UnsuccessfulApiMock}

class HeartbeatActorSpec extends TestKit(ActorSystem()) with ImplicitSender with SpecificationLike {

  val successfulApiMock = new SuccessfulApiMock
  val unsuccessfulApiMock = new UnsuccessfulApiMock

  "Heartbeat Actor" should {
    "successfully check the heartbeat of the API" in {
      // Given
      val parent = TestProbe()
      val actor = TestActorRef[HeartbeatActor](Props(new HeartbeatActor(successfulApiMock)), parent.ref)

      // When
      parent.send(actor, CheckApi)

      // Then
      parent.expectMsg(ApiOk)
      success
    }

    "unsuccessfully check the heartbeat of the API" in {
      // Given
      val parent = TestProbe()
      val actor = TestActorRef[HeartbeatActor](Props(new HeartbeatActor(unsuccessfulApiMock)), parent.ref)

      // When
      parent.send(actor, CheckApi)

      // Then
      parent.expectMsg(ApiNok(500, true, ""))
      success
    }
  }
}
