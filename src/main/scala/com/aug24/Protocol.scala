import akka.actor.ActorRef
object Protocol {

  case class Who()
  case class Move(locator: ActorRef)
  case class Cough(locator: ActorRef)
  case class Moved(location:Location)
  case class Locate(location:Location)
  case class Infect()
  case class GetWell()

}
