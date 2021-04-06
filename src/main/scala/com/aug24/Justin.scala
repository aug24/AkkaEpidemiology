import akka.actor.{ActorSystem, Props, Actor, ActorLogging, ActorRef}
import scala.concurrent.Future
import akka.pattern.pipe
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

import Protocol._

class Justin extends Actor with ActorLogging with Person {

  def who() = Future.successful(this.toString).pipeTo(sender)
  val moveSize = 50
  var infected = false
  var immune = false
  def getInfected(): Boolean = { infected }
  def getImmune(): Boolean = { immune }

  def getWell(stats: ActorRef) = {
    infected = false
    println(s"OMG! I'm healthy again! ($this)")
    stats ! DecreaseCases
  }

  def infectMe(stats: ActorRef) = {
    if (!getImmune()) {
      infected = true
      immune = true
      println(s"OMG! I'm infected ($this)")
      stats ! IncreaseCases
      val getWellTimer = context.system.scheduler.scheduleOnce(2500.milliseconds, self, GetWell(stats))
    }
  }

}
