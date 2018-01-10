package volkovandr.sample.AkkaOverloadSimulator

import akka.actor.{Actor, ActorRef, Props}
import Sender.{Continue, Start, Stop}
import Printer.PrintMessage
import Processor.Message
import Receiver.ReportState
import io.prometheus.client.{Counter, Gauge}

import scala.util.Random

object Sender {
  def props(printer: ActorRef, processor: ActorRef, printEvery: Int): Props = Props(new Sender(printer, processor, printEvery))
  case object Start
  case object Stop
  case object Continue
  case object SlowDown
  case object SpeedUp

  val subjects = Vector("human", "robot", "dog", "house", "pensil", "table", "whale", "hero", "jedi", "ninja")
  val actions = Vector("eats", "catches", "sees", "likes", "hates", "drinks", "smells", "copies", "loses", "admires")
  val objects = Vector("banana", "coconut", "brick", "wall", "computer", "cup", "car", "panda", "pony", "stone", "cow")

  val rnd = new Random()

  def getRandomMessage: String = s"A ${subjects(rnd.nextInt(subjects.size))} ${actions(rnd.nextInt(actions.size))} a ${objects(rnd.nextInt(objects.size))}"

  val SendCounter: Counter = Counter.build()
    .name("messages_sent_total")
    .help("Total number of messages sent by Sender")
    .register()

  val SlownessGauge: Gauge = Gauge.build()
    .name("sender_slowness")
    .help("The current slowness factor of a sender")
    .register()
}

class Sender (printer: ActorRef, processor: ActorRef, printEvery: Int) extends Actor {
  import Sender._

  var counter: Long = 0
  var continue = false
  var slowness: Int = 10

  def generateAndSendMessageToProcessor(): Unit = {
    Thread.sleep(slowness)
    processor ! Message(getRandomMessage)
    SendCounter.inc()
    counter += 1
    if(counter % printEvery == 0)
      printer ! PrintMessage(s"Sent $counter messages. Slowness = $slowness")
  }

  def receive(): PartialFunction[Any, Unit] = {
    case Start =>
      continue = true
      self ! Continue
    case Stop =>
      continue = false
      printer ! PrintMessage(s"Requested to stop! Sent $counter messages. Slowness = $slowness")
    case Continue =>
      generateAndSendMessageToProcessor()
      if(continue) self ! Continue
    case SlowDown =>
      slowness += 1
      SlownessGauge.set(slowness)
      printer ! PrintMessage(s"I've asked to slow down. New slowness factor is $slowness")
    case SpeedUp if slowness > 0 =>
      slowness -= 1
      SlownessGauge.set(slowness)
      printer ! PrintMessage(s"I've asked to speed up. New slowness factor is $slowness")
  }
}