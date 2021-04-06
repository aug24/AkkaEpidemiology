import akka.actor.{ActorSystem, Props, Actor, ActorLogging, ActorRef}
import scala.concurrent.Future
import akka.pattern.pipe
import scala.concurrent.ExecutionContext.Implicits.global

import Protocol._

class Stats extends Actor with ActorLogging {
  
  var infectedCount = 0

  def who() = Future.successful(this.toString).pipeTo(sender)

  def cases(change:Int) = {
    infectedCount+=change
    println(s"Cases are now $infectedCount")
    if (infectedCount==0) context.system.terminate()
  }
  
  def receive = {
    case Who => who()
    case IncreaseCases => cases(1)
    case DecreaseCases => cases(-1)
  }
}

