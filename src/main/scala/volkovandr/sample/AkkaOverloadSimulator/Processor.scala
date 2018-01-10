package volkovandr.sample.AkkaOverloadSimulator

import akka.actor.{Actor, ActorRef, Props}
import Printer.PrintMessage
import io.prometheus.client.Counter

object Processor {
  def props(printer: ActorRef, receiver: ActorRef, printEvery: Int): Props = Props(new Processor(printer, receiver, printEvery))
  case class Message(content: String, timestamp: Long = System.currentTimeMillis())

  val ProcessedCounter: Counter = Counter.build()
    .name("messages_processed_total")
    .help("Total number of messages processed by Processor")
    .register()

}

class Processor (printer: ActorRef, receiver: ActorRef, printEvery: Int) extends Actor {
  import Processor._
  import Receiver._

  var counter: Long = 0

  def processMessage(message: Message): Unit = {
    val lettersCount = message.content.length
    // Very complex processing here
    Thread.sleep(10)
    receiver ! ProcessedMessage(lettersCount, message.timestamp)
    ProcessedCounter.inc()
    counter += 1
    if(counter % printEvery == 0) printer ! PrintMessage(s"Processed $counter messages")
  }

  def receive(): PartialFunction[Any, Unit] = {
    case msg: Message =>
      processMessage(msg)
  }
}
