package patterns
package tagless

import cats.{ Order => OrderZ, _ }
import cats.data._
import cats.implicits._

class LoggingInterpreter[F[_]](implicit me: MonadError[F, Throwable]) extends Logging[F] {

  def info(msg: String): F[Unit] =
    { println(s"Info: $msg") }.pure[F]

  def error(msg: String): F[Unit] =
    { println(s"Error: $msg") }.pure[F]
}



