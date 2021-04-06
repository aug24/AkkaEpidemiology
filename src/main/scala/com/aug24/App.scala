import akka.actor.{ActorSystem, Props, Actor, ActorLogging}
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global
import akka.pattern.ask
import scala.concurrent.duration._
import scala.concurrent.Future
import akka.util.Timeout
import akka.routing.FromConfig

import akka.actor.Actor
import akka.actor.Props
import scala.concurrent.duration._

import Protocol._

object Population extends App {

  implicit val timeout = Timeout(5 second)

  val system = ActorSystem("Population")
  val justin = system.actorOf(FromConfig.props(Props[Justin]), "justin")
  val locator = system.actorOf(FromConfig.props(Props[Locator]), "locator")
  val stats = system.actorOf(FromConfig.props(Props[Stats]), "stats")

  locator ! Start(stats)

  import system.dispatcher

  val moveTimer = system.scheduler.scheduleWithFixedDelay(Duration.Zero, 500.milliseconds, justin, Move(locator))

  // Set up patient zero
  system.scheduler.scheduleOnce(1000.milliseconds, justin, Infect(stats))

  // Then start them coughing...
  val coughTimer = system.scheduler.scheduleWithFixedDelay(Duration.Zero, 50.milliseconds, justin, Cough(locator))

}
