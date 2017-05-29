package com.katsande.akka.broadcast

import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, ClosedShape}
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, RunnableGraph, Sink, Source}
import com.katsande.akka.broadcast.SimpleBroadcastingStream.{Even, Odd}

import scala.concurrent.Future

class SimpleBroadcastingStream(implicit system: ActorSystem) {
  implicit val materialiser = ActorMaterializer()
  implicit val ec = system.dispatcher

  val source: Source[Int, NotUsed] = Source(0 to 10)

  val writeEvenNumbers: Sink[Even, Future[Done]] = Sink.foreach(printEven)
  val writeOddNumbers: Sink[Odd, Future[Done]] = Sink.foreach(printOdd)

  def relu(n: Int): Int = math.max(0, n)

  def run = {
    val graph = RunnableGraph.fromGraph(GraphDSL.create() { implicit b =>
      import GraphDSL.Implicits._
      /**
        *                    -> filterEvenNumbers() -> print()
        *                   |
        * source -> relu() - -> filterOddNumbers()  -> print()
        */
      val bcast = b.add(Broadcast[Int](2))
      source.map(relu) ~> bcast.in
      bcast.out(0) ~> Flow[Int].filter(isEven).map(Even(_)) ~> writeEvenNumbers
      bcast.out(1) ~> Flow[Int].filter(isOdd).map(Odd(_)) ~> writeOddNumbers
      ClosedShape
    })

    graph.run()
  }

  private def isEven(n: Int): Boolean = (n % 2)==0
  private def isOdd(n: Int): Boolean = !isEven(n)

  def printOdd(n: Odd) = Future {
    //Thread.sleep(500)
    println(s"This is an odd number: ${n.n}")
  }
  def printEven(n: Even) = Future {
    //Thread.sleep(400)
    println(s"This is an even number: ${n.n}")
  }
}

object SimpleBroadcastingStream extends App {

  implicit val system = ActorSystem()

  (new SimpleBroadcastingStream).run

  system.terminate()

  case class Even(n: Int)
  case class Odd(n: Int)

}
