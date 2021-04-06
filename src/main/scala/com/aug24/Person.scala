import akka.actor.{ActorSystem, Props, Actor, ActorLogging}
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global
import akka.pattern.ask
import scala.concurrent.duration._
import scala.concurrent.Future
import akka.util.Timeout
import akka.routing.FromConfig

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Props
import scala.concurrent.duration._

import scala.util.Random
import akka.actor.{ActorSystem, Props, Actor, ActorLogging}
import scala.concurrent.Future
import akka.pattern.pipe
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.duration._

trait Person extends Actor with ActorLogging {

  implicit val timeout = Timeout(5 second)

  def moveSize: Int
  def getInfected(): Boolean

  def who()
  def infectMe()
  def getWell()

  import Protocol._

  def receive = {
    case Who => who()
    case Move(locator) => move(locator)
    case Cough(locator) => cough(locator)
    case Infect => infectMe()
    case GetWell => getWell()
  }

  def cough(locator: ActorRef) = {
    if (getInfected()) {
      println(s"$this is infected")
      val location = this.getLocation()
      val infecteesFuture = locator ! Locate(location)
    } else {
      println(s"$this is not infected")
    }
  }

  var locationx = Random.nextInt % 10
  var locationy = Random.nextInt % 10
  def move(locator: ActorRef) = {
    this.locationx = this.locationx + Random.nextInt % moveSize
    this.locationy = this.locationy + Random.nextInt % moveSize
    val location = this.getLocation()
    locator ! Moved(location)
  }
  def getLocation() = Location(locationx, locationy)

}
