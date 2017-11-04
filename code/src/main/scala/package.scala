import java.util.{ Date, Calendar }

import cats._
import cats.free._
import patterns.free.TradingF
import patterns.freecompose.LoggingF

package object patterns {
  final def today = Calendar.getInstance.getTime
  type Trading[A] = Free[TradingF, A]
  type Logging[A] = Free[LoggingF, A]
}
