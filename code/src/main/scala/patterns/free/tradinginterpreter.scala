package patterns
package free

import cats.{ Order => OrderZ, _ }
import cats.data._
import cats.implicits._
import cats.effect.{ Effect, IO }

import model._
import TradeModel._


abstract class TradingInterpreter[F[_]](implicit E: Effect[F]) {
  def apply[A](action: Trading[A]): F[A]
}
  
class TradingIOInterpreter() extends TradingInterpreter[IO] {

  val step: TradingF ~> IO = new (TradingF ~> IO) {

    override def apply[A](fa: TradingF[A]): IO[A] = fa match {
      case FromClientOrder(clientOrder) => makeOrder(clientOrder) match {
        case Left(dv) => IO.raiseError(new Exception(dv.message))
        case Right(o) => IO(o)
      }
      
      case Execute(market, brokerAccount, order) => IO { 
        order.items.map { item =>
          Execution(brokerAccount, item.ins, "e-123", market, item.price, item.qty)
        }
      }

      case Allocate(accounts, executions) => IO { 
        executions.map { execution =>
          val q = execution.quantity / accounts.size
          accounts.map { account =>
            makeTrade(account, execution.instrument, "t-123", execution.market, execution.unitPrice, q)
          }
        }.flatten
      }
    }
  }

  def apply[A](action: Trading[A]): IO[A] = action.foldMap(step)
}
