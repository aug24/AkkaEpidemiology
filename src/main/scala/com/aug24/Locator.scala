import akka.actor.{ActorSystem, Props, Actor, ActorLogging, ActorRef}
import scala.concurrent.Future
import akka.pattern.pipe
import scala.concurrent.ExecutionContext.Implicits.global

class Locator extends Actor with ActorLogging {
  
  val radius = context.system.settings.config.getInt("location.radius");

  // This is rubbish, and needs to be replaced with a QuadTree
  var locations: Map[ActorRef, Location] = Map()
  import Protocol._

  def who() = Future.successful(this.toString).pipeTo(sender)

  def moved(location:Location) = locations += (sender -> location)
  
  def locate(location:Location):List[ActorRef] = {
//    println(locations)
    locations.filter(kv => {
      val (actor, actorLocation) = kv
      ((location.x - actorLocation.x) * (location.x - actorLocation.x)
        + (location.y - actorLocation.y) * (location.y - actorLocation.y)
      ) <= radius * radius
      }
    ).keySet.toList
  }
  
  def receive = {
    case Who => who()
    case Moved(location) => moved(location)
    case Locate(location) => Future.successful(locate(location)).pipeTo(sender)
  }
}

