import akka.actor.{ActorSystem, Props, Actor, ActorLogging, ActorRef}
import scala.concurrent.Future
import akka.pattern.pipe
import scala.concurrent.ExecutionContext.Implicits.global

class Locator extends Actor with ActorLogging {
  
  var statsMaybe: Option[ActorRef] = None
  val radius = context.system.settings.config.getInt("location.radius");

  // This is rubbish, and needs to be replaced with a QuadTree
  var locations: Map[ActorRef, Location] = Map()
  import Protocol._

  def who() = Future.successful(this.toString).pipeTo(sender)

  def moved(location:Location) = locations += (sender -> location)
  def saveStats(stats:ActorRef) = this.statsMaybe = Some(stats)
  
  def locate(location:Location) = {
    statsMaybe.foreach( stats => 
      locations.filter(kv => {
        val (actor, actorLocation) = kv
        ((location.x - actorLocation.x) * (location.x - actorLocation.x)
          + (location.y - actorLocation.y) * (location.y - actorLocation.y)
        ) <= radius * radius
        }
      ).keySet.foreach(infectee => infectee ! Infect(stats))
    )
  }
  
  def receive = {
    case Who => who()
    case Moved(location) => moved(location)
    case Locate(location) => locate(location)
    case Start(stats) => saveStats(stats)
  }
}

