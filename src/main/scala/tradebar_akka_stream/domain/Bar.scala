package tradebar_akka_stream.domain

object Bar {

  def calculateStartTime(intervalSizeMs: Long)(trade: Trade): Long = {
    (trade.time / intervalSizeMs) * intervalSizeMs
  }

  def apply(intervalSizeMs: Long, trade: Trade): Bar =
    Bar(calculateStartTime(intervalSizeMs)(trade),
        intervalSizeMs, trade.price,
        trade.price,
        trade.price,
        trade.price,
        trade.volume)

}

case class Bar(startTime: Long,
               intervalSizeMs: Long,
               open: Double,
               high: Double,
               low: Double,
               close: Double,
               volume: Double) {

  def update(trade: Trade): Bar =
    this.copy(high = Math.max(this.high, trade.price),
      low = Math.min(this.high, trade.price),
      close = trade.price,
      volume = this.volume + trade.volume)
}