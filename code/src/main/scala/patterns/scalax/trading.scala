package patterns
package scalax

import cats.{ Order => OrderZ, _ }
import cats.data._
import cats.implicits._

import model._

/**
 *  Use pluggable effects - can work with cats-effect IO - fully RT
 */  
trait Trading[F[_], Account, Trade, ClientOrder, Order, Execution, Market] {

  def fromClientOrder: ClientOrder => F[Order]
  def execute(market: Market, brokerAccount: Account): Order => F[List[Execution]]
  def allocate(accounts: List[Account]): List[Execution] => F[List[Trade]]
}

trait TradingK[F[_], Account, Trade, ClientOrder, Order, Execution, Market] {

  def fromClientOrder: Kleisli[F, ClientOrder, Order]
  def execute(market: Market, brokerAccount: Account): Kleisli[F, Order, List[Execution]]
  def allocate(accounts: List[Account]): Kleisli[F, List[Execution], List[Trade]]
}

