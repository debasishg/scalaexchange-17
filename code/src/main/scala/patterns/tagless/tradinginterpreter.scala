package patterns
package tagless

import java.util.{ Date, Calendar }

import cats.{ Order => OrderZ, _ }
import cats.data._
import cats.implicits._
import cats.effect.IO

import model._
import TradeModel._

class TradingInterpreter[F[_]](implicit me: MonadError[F, Throwable]) 
  extends Trading[F, Account, Trade, ClientOrder, Order, Execution, Market] {

  def fromClientOrder = Kleisli[F, ClientOrder, Order] { 
    makeOrder(_) match {
      case Left(dv) => me.raiseError(new Exception(dv.message))
      case Right(o) => o.pure[F]
    }
  }

  def execute(market: Market, brokerAccount: Account) = Kleisli[F, Order, List[Execution]] { order =>
    order.items.map { item =>
      Execution(brokerAccount, item.ins, "e-123", market, item.price, item.qty)
    }.pure[F]
  }

  def allocate(accounts: List[Account]) = Kleisli[F, List[Execution], List[Trade]] { executions =>
    executions.map { execution =>
      val q = execution.quantity / accounts.size
      accounts.map { account =>
        makeTrade(account, execution.instrument, "t-123", execution.market, execution.unitPrice, q)
      }
    }.flatten.pure[F]
  }
}

object TradingInterpreter extends TradingInterpreter

