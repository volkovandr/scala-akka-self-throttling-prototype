package volkovandr.sample.AkkaOverloadSimulator

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import Printer.PrintMessage
import Throttler.ProcessedMessageTime
import io.prometheus.client.{Counter, Gauge}

object Receiver {
  def props(printer: ActorRef, printEvery: Int): Props = Props(new Receiver(printer, printEvery))
  case class ProcessedMessage(count: Int, timestamp: Long)
  case object ReportState
  case class TakeThrottler(throttler: ActorRef)

  val ReceivedCounter: Counter = Counter.build()
    .name("messages_received_total")
    .help("Total number of messages received by Receiver")
    .register()
}

class Receiver(printer: ActorRef, printEvery: Int) extends Actor with ActorLogging {
  import Receiver._

  var counter: Long = 0
  var delayedCounter: Long = 0
  var totalLetters: Long = 0
  var lastIn: Long = 0
  var lastOut:Long = 0

  var throttler: ActorRef = _

  def receiveMessage(message: ProcessedMessage): Unit = {
    totalLetters += message.count
    counter += 1
    if(counter % printEvery == 0)
      printer ! PrintMessage(s"Received $counter messages. Total number of letters is $totalLetters")
    throttler ! ProcessedMessageTime(message.timestamp)
    ReceivedCounter.inc()
  }

  def receive(): PartialFunction[Any, Unit] = {
    case msg: ProcessedMessage =>
      receiveMessage(msg)
    case ReportState =>
      printer ! PrintMessage(s"Reporting state. Total number of messages: $counter. Total number of letters: $totalLetters")
    case TakeThrottler(s) =>
      throttler = s
  }
}
