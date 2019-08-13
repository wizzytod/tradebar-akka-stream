package tradebar_akka_stream.stream

import akka.stream.stage._
import akka.stream.{Attributes, FlowShape, Inlet, Outlet}
import tradebar_akka_stream.domain.{Bar, Trade}

case class GroupTradesToBars(intervalSizeMs: Long, calculateStartTime: Trade => Long) extends GraphStage[FlowShape[Trade, Bar]] {
  val in = Inlet[Trade]("GroupTradesToBars.in")
  val out = Outlet[Bar]("GroupTradesToBars.out")

  override val shape: FlowShape[Trade, Bar] = FlowShape.of(in, out)

  override def createLogic(attr: Attributes): GraphStageLogic =
    new GraphStageLogic(shape) with StageLogging {

      var last: Option[Bar] = None

      setHandler(in, new InHandler {
        override def onPush(): Unit = {
          val trade = grab(in)
          val startTime = calculateStartTime(trade)
          last match {
            case Some(v) =>
              if (v.startTime == startTime) {
                last = Some(v.update(trade))
                pull(in)
              } else {
                emit(out, v)
                last = Some(Bar(intervalSizeMs, trade ))
              }
            case None =>
              last = Some(Bar(intervalSizeMs, trade ))
              pull(in)
          }
        }

        override def onUpstreamFinish(): Unit = {
          last.foreach(bar => emit(out, bar))
          last = None
          complete(out)
        }
      })

      setHandler(out, new OutHandler {
        override def onPull(): Unit = {
          pull(in)
        }
      })
    }
}
