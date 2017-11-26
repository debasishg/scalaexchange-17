package patterns
package tagless

import cats.{ Order => OrderZ, _ }
import cats.data._
import cats.implicits._

import model._
import TradeModel._

trait Trading[F[_]] {
  def fromClientOrder(clientOrder: ClientOrder): F[Order]
  def execute(market: Market, brokerAccount: Account, order: Order): F[List[Execution]]
  def allocate(accounts: List[Account], executions: List[Execution]): F[List[Trade]]
}
