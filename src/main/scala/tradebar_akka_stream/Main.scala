package tradebar_akka_stream

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Keep, Sink, Source}
import tradebar_akka_stream.domain.{Bar, Trade}
import tradebar_akka_stream.stream.GroupTradesToBars

import scala.concurrent.ExecutionContext.Implicits._


object HelloWorld {

  val ex: List[Trade] = List(Trade(1, 4, 3), Trade(2, 6, 4), Trade(3, 7, 4))

  implicit val actorSystem = ActorSystem("default-actor-system")

  implicit val materializer = ActorMaterializer()

  val stream = Source(ex)

  val intervalSizeMs = 4

  val groupingStage = GroupTradesToBars(intervalSizeMs, Bar.calculateStartTime(intervalSizeMs))

  def main(args: Array[String]): Unit = {
    println("scala collections result:")
    println(toBars(ex, 4))
    println("akka stream result:")
    val future = stream.via(groupingStage).toMat(Sink.collection)(Keep.right).run()
    future.foreach(println)
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








