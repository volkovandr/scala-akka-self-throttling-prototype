//#full-example
package volkovandr.sample.AkkaOverloadSimulator

import akka.actor.ActorSystem
import akka.testkit.TestKit
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, Matchers}


class SenderSpecs(_system: ActorSystem)
  extends TestKit(_system)
    with Matchers
    with FlatSpecLike
    with BeforeAndAfterAll {

  def this() = this(ActorSystem("AkkaQuickstartSpec"))

  override def afterAll: Unit = {
    shutdown(system)
  }

  "A Sender" should "be able to generate random messages" in {
    val message1 = Sender.getRandomMessage
    val message2 = Sender.getRandomMessage
    val message3 = Sender.getRandomMessage
    assert(message1 != message2 || message2 != message3 || message1 != message3)
  }

}
