package ski.bedrit.stock

import akka.actor.{ActorSystem, Props}
import ski.bedrit.stock.actors.HeartbeatActor
import ski.bedrit.stock.actors.HeartbeatActor.CheckApi
import ski.bedrit.stock.api.RestApi

import scala.concurrent.Await
import scala.concurrent.duration._

object Main extends App {
  implicit val system = ActorSystem("MyActorSystem")

  val api = new RestApi

  val actor = system.actorOf(Props(new HeartbeatActor(api)))

  actor ! CheckApi

  Await.result(system.whenTerminated, 1 minute)
}
