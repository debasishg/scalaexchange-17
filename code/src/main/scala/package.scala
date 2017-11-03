import java.util.{ Date, Calendar }

import cats._
import cats.free._
import patterns.free.TradingF

package object patterns {
  final def today = Calendar.getInstance.getTime
  type Trading[A] = Free[TradingF, A]
}
