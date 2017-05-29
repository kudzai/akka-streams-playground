package com.katsande.akka.kafka

import akka.actor.ActorSystem
import akka.testkit.TestKit
import net.manub.embeddedkafka.{EmbeddedKafka, EmbeddedKafkaConfig}
import org.apache.kafka.common.serialization.StringDeserializer
import org.scalatest._
import org.scalatest.concurrent.Eventually

class SimpleProducerStreamSpec extends TestKit(ActorSystem("simple-source")) with FunSpecLike with EmbeddedKafka with BeforeAndAfterAll with Eventually with Matchers {
  val simpleSource = new SimpleProducerStream()
  implicit val config = EmbeddedKafkaConfig(kafkaPort = 12345)
  implicit val deserialiser = new StringDeserializer()

  override def beforeAll(): Unit = {
    super.beforeAll()
    EmbeddedKafka.start()
  }

  override def afterAll() = {
    EmbeddedKafka.stop()
    super.afterAll()
  }

  describe("Simple stream producer") {
    it("should emit messages to kafka topic") {
      simpleSource.run
      val expected = (1 to 20).map(i => s"message $i")

      eventually {
        val messages = consumeNumberMessagesFrom("mytest", 20, true)
        messages should contain theSameElementsInOrderAs expected
      }
    }
  }


}
