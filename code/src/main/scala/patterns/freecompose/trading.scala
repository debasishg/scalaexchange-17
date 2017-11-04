package patterns
package freecompose

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
  def fromClientOrder(clientOrder: ClientOrder) = 
    Free.liftF(FromClientOrder(clientOrder))

  def execute(market: Market, brokerAccount: Account, order: Order) = 
    Free.liftF(Execute(market, brokerAccount, order))

  def allocate(accounts: List[Account], executions: List[Execution]) = 
    Free.liftF(Allocate(accounts, executions))

  def tradeGeneration(clientOrder: ClientOrder, market: Market, brokerAccount: Account, accounts: List[Account]) = 
    for {
      order       <- fromClientOrder(clientOrder) 
      executions  <- execute(market, brokerAccount, order) 
      trades      <- allocate(accounts, executions)
    } yield trades
}

object TradingService extends TradingService

class TradingI[F[_]](implicit I: InjectK[TradingF, F]) {
  type FreeF[A] = Free[F, A]

  def fromClientOrderI(clientOrder: ClientOrder) = 
    Free.inject[TradingF, F](FromClientOrder(clientOrder))

  def executeI(market: Market, brokerAccount: Account, order: Order) = 
    Free.inject[TradingF, F](Execute(market, brokerAccount, order))

  def allocateI(accounts: List[Account], executions: List[Execution]) = 
    Free.inject[TradingF, F](Allocate(accounts, executions))
}

object TradingI {
  implicit def tradingI[F[_]](implicit I: InjectK[TradingF, F]): TradingI[F] = new TradingI[F]
}  
