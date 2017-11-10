package patterns
package scalax

import model._

/**
 *  Use pluggable effects - can work with cats-effect IO - fully RT
 */  
trait Trading[F[_], Account, Trade, ClientOrder, Order, Execution, Market] {

  def fromClientOrder: ClientOrder => F[Order]
  def execute(market: Market, brokerAccount: Account): Order => F[List[Execution]]
  def allocate(accounts: List[Account]): List[Execution] => F[List[Trade]]
}

