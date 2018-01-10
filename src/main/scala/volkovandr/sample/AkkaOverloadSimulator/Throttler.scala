package volkovandr.sample.AkkaOverloadSimulator

import akka.actor.{Actor, ActorRef, Props}
import io.prometheus.client.Gauge
import volkovandr.sample.AkkaOverloadSimulator.Printer.PrintMessage
import volkovandr.sample.AkkaOverloadSimulator.Sender.{SlowDown, SpeedUp}

import scala.collection.mutable

object Throttler {
  def props(printer: ActorRef, senderActor: ActorRef): Props = Props(new Throttler(printer, senderActor))
  case class ProcessedMessageTime(timestamp: Long)

  val InputDelayGauge: Gauge = Gauge.build()
    .name("messages_input_delay_ms")
    .help("Sender input delay")
    .register()

  val OutputDelayGauge: Gauge = Gauge.build()
    .name("messages_output_delay_ms")
    .help("Receiver output delay")
    .register()
}

class Throttler(printer: ActorRef, senderActor: ActorRef) extends Actor {
  import Throttler._

  var iFirst: Long = 0
  var iLast: Long = 0
  var oFirst: Long = 0
  var oLast: Long = 0
  var counter: Long = 0

  def receive(): PartialFunction[Any, Unit] = {
    case ProcessedMessageTime(timestamp) =>
      if(counter == 0) {
        iFirst = timestamp
        oFirst = System.currentTimeMillis()
        counter += 1
      }
      else if(counter == 1000) {
        iLast = timestamp
        oLast = System.currentTimeMillis()
        val deltaI = iLast - iFirst
        val deltaO = oLast - oFirst
        if(deltaO > deltaI) senderActor ! SlowDown
        else if (deltaO < deltaI) senderActor ! SpeedUp
        InputDelayGauge.set(deltaI)
        OutputDelayGauge.set(deltaO)
        printer ! PrintMessage(s"DeltaI = $deltaI, DeltaO = $deltaO")
        counter = 0
      }
      else counter += 1
  }
}
