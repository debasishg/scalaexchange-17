import java.util.{ Date, Calendar }

import cats._
import cats.data._
import cats.data.Validated._
import cats.implicits._

package object patterns {
  final def today = Calendar.getInstance.getTime
}
