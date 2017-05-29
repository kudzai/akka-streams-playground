package com.katsande.akka.kafka

import akka.actor.ActorSystem
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import org.apache.kafka.common.serialization.StringDeserializer

class SimpleSourceStream(implicit system: ActorSystem) {

  implicit val materialiser = ActorMaterializer()

  val consumerSettings = ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
//    .withBootstrapServers("localhost:9092")
//    .withGroupId("group1")
//    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

  val source = Consumer.plainSource(consumerSettings, Subscriptions.topics("mytest"))

  def run = source.runWith(Sink.foreach(println))
}

object SimpleSourceStream extends App {
  implicit val system = ActorSystem("testSys")

  (new SimpleSourceStream).run
}
