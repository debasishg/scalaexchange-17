package patterns
package tagless

import model._

/**
 *  Use pluggable effects - can work with cats-effect IO - fully RT
 */  
trait Trading[F[_], Account, Trade, ClientOrder, Order, Execution, Market] {

  def fromClientOrder(clientOrder: ClientOrder): F[Order]
  def execute(market: Market, brokerAccount: Account, order: Order): F[List[Execution]]
  def allocate(accounts: List[Account], executions: List[Execution]): F[List[Trade]]
}

