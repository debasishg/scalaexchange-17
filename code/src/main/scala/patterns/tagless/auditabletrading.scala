package patterns
package tagless

import cats.{ Order => OrderZ, _ }
import cats.data._
import cats.implicits._

import model._
import TradeModel._

final class AuditableTrading[M[_]: Applicative](trading: Trading[M])
  extends Trading[WriterT[M, Vector[String], ?]] {

  def fromClientOrder(clientOrder: ClientOrder): WriterT[M, Vector[String], Order] =
    WriterT.lift(trading.fromClientOrder(clientOrder))

  def execute(market: Market, brokerAccount: Account, order: Order): WriterT[M, Vector[String], List[Execution]] =

    WriterT.putT(trading.execute(market, brokerAccount, order))(Vector(s"Generating executions for Order $order Broker Account $brokerAccount and Market $market"))

  def allocate(accounts: List[AccountNo], executions: List[Execution]): WriterT[M, Vector[String], List[Trade]] =
    WriterT.putT(trading.allocate(accounts, executions))(executions.map(_.toString).toVector)
}
