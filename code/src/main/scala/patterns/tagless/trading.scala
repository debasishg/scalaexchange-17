package patterns
package tagless

import cats.{ Order => OrderZ, _ }
import cats.data._
import cats.implicits._

import model._
import TradeModel._

trait Trading[F[_]] {
  def fromClientOrder: ClientOrder => F[Order]
  def execute(market: Market, brokerAccount: Account): Order => F[List[Execution]]
  def allocate(accounts: List[Account]): List[Execution] => F[List[Trade]]
}
