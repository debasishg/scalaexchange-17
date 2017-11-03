package patterns
package free

import cats.{ Order => OrderZ, _ }
import cats.data._
import cats.free._
import cats.free.Free._
import cats.implicits._

import model._
import TradeModel._

sealed trait TradingF[A]

case class FromClientOrder(clientOrder: ClientOrder) extends TradingF[Order]
case class Execute(market: Market, brokerAccount: Account, order: Order) extends TradingF[List[Execution]]
case class Allocate(accounts: List[Account], executions: List[Execution]) extends TradingF[List[Trade]]

trait TradingService {
  def fromClientOrder = Kleisli[Trading, ClientOrder, Order] { clientOrder => 
    Free.liftF(FromClientOrder(clientOrder))
  }

  def execute(market: Market, brokerAccount: Account) = Kleisli[Trading, Order, List[Execution]] { order =>
    Free.liftF(Execute(market, brokerAccount, order))
  }

  def allocate(accounts: List[Account]) = Kleisli[Trading, List[Execution], List[Trade]] { executions =>
    Free.liftF(Allocate(accounts, executions))
  }

  def tradeGeneration(market: Market, brokerAccount: Account, accounts: List[Account]) =
    fromClientOrder andThen execute(market, brokerAccount) andThen allocate(accounts)
}

object TradingService extends TradingService
