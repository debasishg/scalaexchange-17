package patterns
package freecompose

import cats.{ Order => OrderZ, _ }
import cats.data._
import cats.implicits._
import cats.effect.{ Effect, IO }

abstract class LoggingInterpreter[F[_]](implicit E: Effect[F]) {
  def step: LoggingF ~> IO
}
  
object LoggingIOInterpreter extends LoggingInterpreter[IO] {
  val step: LoggingF ~> IO = {
    new (LoggingF ~> IO) {
      def apply[A](fa: LoggingF[A]): IO[A] = fa match {
        case Info(msg)  => IO { println(s"[Info] - $msg") } 
        case Error(msg) => IO { println(s"[Error] - $msg") }
      }
    }
  }
}

