package com.katsande.akka.complex

import akka.Done
import akka.actor.ActorSystem
import akka.stream.scaladsl.{GraphDSL, Merge, Sink, Source}
import akka.stream.{ActorMaterializer, SourceShape}

import scala.concurrent.Future

class GraphBasedSourceStream(implicit system: ActorSystem) {
  implicit val materialiser = ActorMaterializer()

  def run: Future[Done] = {
    // Simple way to create a graph backed Source
    val source = Source.fromGraph( GraphDSL.create() { implicit builder =>
      import GraphDSL.Implicits._
      val merge = builder.add(Merge[Any](2))
      Source(List("test", "another")) ~> merge
      Source(List(2, 3, 4)) ~> merge

      // Exposing exactly one output port
      SourceShape(merge.out)
    })
    source.map(_.toString).runWith(Sink.foreach(println))
  }
}

object GraphBasedSourceStream extends App {
  implicit val system = ActorSystem("TestStream")
  implicit val ec = system.dispatcher

  (new GraphBasedSourceStream).run.onComplete(_ => system.terminate())
}
