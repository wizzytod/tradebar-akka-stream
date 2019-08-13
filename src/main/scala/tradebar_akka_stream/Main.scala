package tradebar_akka_stream

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Keep, Sink, Source}
import akka.stream.{ActorMaterializer, KillSwitches}
import tradebar_akka_stream.domain.{Bar, Trade}
import tradebar_akka_stream.stream.GroupTradesToBars


import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits._


object HelloWorld {

  val ex: List[Trade] = List(Trade(1, 4, 3), Trade(2, 6, 4), Trade(3, 7, 4))

  implicit val actorSystem = ActorSystem("default-actor-system")

  implicit val materializer = ActorMaterializer()

  val stream = Source(ex)

  val intervalSizeMs = 3

  val groupingStage = GroupTradesToBars(intervalSizeMs, Bar.calculateStartTime(intervalSizeMs))

  def main(args: Array[String]): Unit = {
    println("scala collections result:")
    println(toBars(ex, intervalSizeMs))
    println("akka stream result:")
    val future  = stream.via(groupingStage)
      .toMat(Sink.foreach(println))(Keep.right)
      .run()

    Await.result(future, 3.second)
    actorSystem.terminate()

  }


  def toBars(trades: Seq[Trade], intervalSizeMs: Long): Seq[Bar] = {

    trades
      .groupBy(Bar.calculateStartTime(intervalSizeMs))
      .map[Bar] {
        case (startTime: Long, value: List[Trade]) =>
          val sortedTrades = value.sortBy(_.time)

          domain.Bar(startTime,
            intervalSizeMs,
            sortedTrades.head.price,
            value.map(_.price).max,
            value.map(_.price).min,
            sortedTrades.last.price,
            value.map(_.volume).sum)
      }
      .toList

  }

}








