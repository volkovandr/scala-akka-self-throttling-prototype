package volkovandr.sample.AkkaOverloadSimulator

import akka.actor.{ActorRef, ActorSystem}
import Sender._
import Receiver._
import com.typesafe.config.ConfigFactory

object Main extends App {

  val conf = ConfigFactory.load()
  val printEvery = conf.getInt("print-every")

  val system: ActorSystem = ActorSystem("MessagesSendProcessReceive")

  val printer: ActorRef = system.actorOf(Printer.props, "Printer")
  val receiver: ActorRef = system.actorOf(Receiver.props(printer, printEvery), "Receiver")
  val processor: ActorRef = system.actorOf(Processor.props(printer, receiver, printEvery), "Processor")
  val sender: ActorRef = system.actorOf(Sender.props(printer, processor, printEvery), "Sender")
  val throttler: ActorRef = system.actorOf(Throttler.props(printer, sender), "Throttler")

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
