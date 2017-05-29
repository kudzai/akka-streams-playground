package com.katsande.akka.kafka

import akka.actor.ActorSystem
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.{ActorMaterializer, ThrottleMode}
import akka.stream.scaladsl.{Flow, Source}
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{ByteArraySerializer, StringSerializer}

import scala.concurrent.Future
import scala.util.{Failure, Try}
import scala.concurrent.duration._

class StreamWithSharedProducer(implicit system: ActorSystem) {
  implicit val actorMaterialiser = ActorMaterializer()

  val producerSettings = ProducerSettings(system, new ByteArraySerializer, new StringSerializer)

  val sharedSource = Source(1 to 20)
  val flowWithThrottle = Flow[Int]
    .throttle(1, 1 second, 1, ThrottleMode.shaping)
    .map(in => s"this is the first flow $in")
    .map { new ProducerRecord[Array[Byte], String]("mytest", _) }

  val flowWithFilter = Flow[Int]
    .filter(_ % 2 == 0)
    .map(in => s"this is the second flow $in")
    .map { new ProducerRecord[Array[Byte], String]("myothertest", _)}

  lazy val sharedProducer = producerSettings.createKafkaProducer()

  def run = (
    sharedSource.async.via(flowWithThrottle).runWith(Producer.plainSink(producerSettings, sharedProducer)),
    sharedSource.async.via(flowWithFilter).runWith(Producer.plainSink(producerSettings, sharedProducer))
  )
}

object StreamWithSharedProducer extends App {
  implicit val system = ActorSystem("shared-producer-system")
  Try {
    implicit val ec = system.dispatcher

    val sp = new StreamWithSharedProducer

    val (done1, done2) = sp.run

    Future.sequence(List(done1, done2)).onComplete(_ => system.terminate())
  } match {
    case Failure(e) =>
      system.terminate()
      throw e
    case _ =>
  }
}
