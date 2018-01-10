package volkovandr.sample.AkkaOverloadSimulator

import akka.actor.{ActorRef, ActorSystem}
import Sender._
import Receiver._

object Main extends App {
  val system: ActorSystem = ActorSystem("MessagesSendProcessReceive")

  val printer: ActorRef = system.actorOf(Printer.props, "printer")
  val receiver: ActorRef = system.actorOf(Receiver.props(printer, 100000))
  val processor: ActorRef = system.actorOf(Processor.props(printer, receiver, 100000))
  val sender: ActorRef = system.actorOf(Sender.props(printer, processor, 100000))
  val throttler: ActorRef = system.actorOf(Throttler.props(printer, sender))

  Metrics.initMetrics()

  receiver ! TakeThrottler(throttler)

  sender ! Start

  sys.addShutdownHook {
    sender ! Stop
    receiver ! ReportState
    Thread.sleep(5000)
    system.terminate()
    Metrics.shutdownMetrics()
  }

}
