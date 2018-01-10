package volkovandr.sample.AkkaOverloadSimulator

import akka.actor.{Actor, ActorLogging, Props}

object Printer {
  def props: Props = Props[Printer]

  final case class PrintMessage(message: String)
}

class Printer extends Actor with ActorLogging {
  import Printer._

  def receive: PartialFunction[Any, Unit] = {
    case PrintMessage(message) =>
      log.info(s"Message from ${sender()}: $message")
  }
}
