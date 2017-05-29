package com.katsande.akka.basic

import akka.Done
import akka.actor.ActorSystem
import akka.stream.scaladsl.Source
import akka.stream.{ActorMaterializer, ThrottleMode}

import scala.concurrent.Future
import scala.concurrent.duration._
class SimpleThrottlingStream(implicit system: ActorSystem) {
  implicit val materialiser = ActorMaterializer()

  def run: Future[Done] = {
    val source = Source(1 to 5)
    val factorials = source.scan(BigInt(1))((acc, next) => acc * next)
    factorials
      .zipWith(Source(0 to 100))((num, idx) => s"$idx! = $num")
      .throttle(1, 1.second, 1, ThrottleMode.shaping)
      .runForeach(println)
  }
}

object SimpleThrottlingStream extends App {
  implicit val system = ActorSystem("simple-stream-system")
  implicit val ec = system.dispatcher

  (new SimpleThrottlingStream).run.onComplete(_ => system.terminate())
}
