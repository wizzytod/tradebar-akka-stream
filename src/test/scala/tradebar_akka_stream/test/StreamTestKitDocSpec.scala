package tradebar_akka_stream.test

import akka.stream._
import akka.stream.scaladsl._
import akka.testkit.AkkaSpec
import tradebar_akka_stream.domain
import tradebar_akka_stream.domain.{Bar, Trade}
import tradebar_akka_stream.stream.GroupTradesToBars

import scala.concurrent._
import scala.concurrent.duration._

class StreamTestKitDocSpec extends AkkaSpec {

  implicit val materializer = ActorMaterializer()


  "strict collection" in {
    //#strict-collection

    val stream = Source(List (Trade(1, 4, 3), Trade(2, 6, 4), Trade(3, 7, 4), Trade(4, 7, 4)))
    val expectedResult = List(Bar(0,4,4.0,7.0,6.0,7.0,11.0), domain.Bar(4,4,7.0,7.0,7.0,7.0,4.0))

    val intervalSizeMs = 4

    val groupingStage = GroupTradesToBars(intervalSizeMs, Bar.calculateStartTime(intervalSizeMs))

    val future = stream.via(groupingStage).toMat(Sink.collection)(Keep.right).run()
    val result = Await.result(future, 3.seconds)
    assert(result == expectedResult)
    //#strict-collection
  }
}