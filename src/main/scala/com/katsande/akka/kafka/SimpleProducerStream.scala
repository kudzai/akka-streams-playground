package com.katsande.akka.kafka

import akka.actor.ActorSystem
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Source}
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{ByteArraySerializer, StringSerializer}

class SimpleProducerStream(implicit system: ActorSystem) {
  implicit val actorMaterialiser = ActorMaterializer()

  val producerSettings = ProducerSettings(system, new ByteArraySerializer, new StringSerializer)

  val source = Source(1 to 20)
  val flow = Flow[Int]
    .map(i => s"message $i")
    .map { new ProducerRecord[Array[Byte], String]("mytest", _)}

  val sink = Producer.plainSink(producerSettings)

  def run = source.via(flow).runWith(sink)
}

object SimpleProducerStream extends App {
  implicit val system = ActorSystem("producer-system")
  implicit val ec = system.dispatcher

  val done = (new SimpleProducerStream).run
  done.onComplete(_ => system.terminate())
}
