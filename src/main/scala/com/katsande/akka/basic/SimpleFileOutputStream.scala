package com.katsande.akka.basic

import java.nio.file.Paths

import akka.actor.ActorSystem
import akka.stream.scaladsl.{FileIO, Flow, Keep, Sink, Source}
import akka.stream.{ActorMaterializer, IOResult}
import akka.util.ByteString

import scala.concurrent.Future
class SimpleFileOutputStream(implicit system: ActorSystem) {
  implicit val materialiser = ActorMaterializer()

  def run: Future[IOResult] = {
    val source = Source(100 to 110)
    source.map(_.toString).runWith(lineSink("target/test.txt"))
  }

  private def lineSink(filename: String): Sink[String, Future[IOResult]] =
    Flow[String]
      .map(s => ByteString(s + "\n"))
      .toMat(FileIO.toPath(Paths.get(filename)))(Keep.right)
}

object SimpleFileOutputStream extends App {
  implicit val system = ActorSystem("simple-stream-system")
  implicit val ec = system.dispatcher

  (new SimpleFileOutputStream).run.onComplete(_ => system.terminate())
}
