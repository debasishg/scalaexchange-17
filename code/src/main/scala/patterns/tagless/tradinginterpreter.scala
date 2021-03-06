package patterns
package tagless

import cats.{ Order => OrderZ, _ }
import cats.data._
import cats.implicits._

import model._
import TradeModel._

class TradingInterpreter[F[_]](implicit me: MonadError[F, Throwable]) 
  extends Trading[F] {

  def fromClientOrder(clientOrder: ClientOrder): F[Order] = makeOrder(clientOrder) match {
    case Left(dv) => me.raiseError(new Exception(dv.message))
    case Right(o) => o.pure[F]
  }

  def execute(market: Market, brokerAccount: Account, order: Order): F[List[Execution]] = order.items.map { item =>
    Execution(brokerAccount, item.ins, "e-123", market, item.price, item.qty)
  }.pure[F]

  def allocate(accounts: List[Account], executions: List[Execution]): F[List[Trade]] = executions.map { execution =>
    val q = execution.quantity / accounts.size
    accounts.map { account =>
      makeTrade(account, execution.instrument, "t-123", execution.market, execution.unitPrice, q)
    }
  }.flatten.pure[F]
}
