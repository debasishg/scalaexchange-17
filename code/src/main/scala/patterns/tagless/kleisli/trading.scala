package patterns
package tagless.kleisli

import cats.{ Order => OrderZ, _ }
import cats.data._
import cats.implicits._

import model._
import TradeModel._

trait Trading[F[_]] {
  def fromClientOrder: Kleisli[F, ClientOrder, Order]
  def execute(market: Market, brokerAccount: Account): Kleisli[F, Order, List[Execution]]
  def allocate(accounts: List[AccountNo]): Kleisli[F, List[Execution], List[Trade]]
}

