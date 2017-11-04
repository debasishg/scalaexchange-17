package patterns
package freecompose

import cats.{ Order => OrderZ, _ }
import cats.data._
import cats.free._
import cats.free.Free._
import cats.implicits._

sealed trait LoggingF[A]

case class Info(msg: String) extends LoggingF[Unit]
case class Error(msg: String) extends LoggingF[Unit]

trait LoggingService {

  def info(msg: String): Logging[Unit] =
    liftF(Info(msg))

  def error(msg: String): Logging[Unit] =
    liftF(Error(msg))
}

object LoggingService extends LoggingService

class LoggingI[F[_]](implicit I: InjectK[LoggingF, F]) {
  def infoI(msg: String): Free[F, Unit] = Free.inject[LoggingF, F](Info(msg))

  def errorI(msg: String): Free[F, Unit] = Free.inject[LoggingF, F](Error(msg))
}

object LoggingI {
  implicit def loggingI[F[_]](implicit I: InjectK[LoggingF, F]): LoggingI[F] = new LoggingI[F]
}
