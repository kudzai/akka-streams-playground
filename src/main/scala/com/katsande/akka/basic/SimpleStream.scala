package com.katsande.akka.basic

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Keep, RunnableGraph, Sink, Source}

import scala.concurrent.Future
class SimpleStream(implicit system: ActorSystem) {
  implicit val materialiser = ActorMaterializer()

  def run = {
    val source = Source(1 to 10)
    val sink = Sink.fold[Int, Int](0)(_ + _)

    // connect the Source to the Sink, obtaining a RunnableGraph
    val runnable: RunnableGraph[Future[Int]] = source.toMat(sink)(Keep.right)

    // materialize the flow and get the value of the FoldSink
    val sum: Future[Int] = runnable.run()

    sum.foreach(println)(system.dispatcher)

    sum
  }
}

object SimpleStream extends App {
  implicit val system = ActorSystem("simple-stream-system")
  implicit val ec = system.dispatcher

  (new SimpleStream).run.onComplete(_ => system.terminate())
}
