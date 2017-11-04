package patterns
package tagless

import cats._
import cats.data._
import cats.implicits._
import cats.effect.IO

import model._

/**
 *  Use pluggable effects - can work with cats-effect IO - fully RT
 */  
trait Trading[F[_], Account, Trade, ClientOrder, Order, Execution, Market] {

  def fromClientOrder: Kleisli[F, ClientOrder, Order]
  def execute(market: Market, brokerAccount: Account): Kleisli[F, Order, List[Execution]]
  def allocate(accounts: List[Account]): Kleisli[F, List[Execution], List[Trade]]

  def tradeGeneration(
    market: Market, 
    broker: Account, 
    clientAccounts: List[Account])(implicit F: Monad[F]): Kleisli[F, ClientOrder, List[Trade]] =
    fromClientOrder andThen execute(market, broker) andThen allocate(clientAccounts)
}

