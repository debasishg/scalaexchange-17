package patterns
package freecompose

import cats.{ Order => OrderZ, _ }
import cats.data._
import cats.free._
import cats.implicits._
import cats.effect.IO
import cats.data.EitherK


import model._
import TradeModel._

object Main {
  import TradingService._
  import LoggingService._

  val m1 = makeMarket("Tokyo")
  val m2 = makeMarket("NewYork")
  val m3 = makeMarket("Singapore")

  val ba = "broker-account-1"

  val ca1 = "client-account-1"
  val ca2 = "client-account-2"
  val ca3 = "client-account-3"

  val cor = Map(
    "no" -> "client-order-1",
    "customer" -> "customer-1",
    "instrument" -> "ibm/100/1000-google/200/2000"
  )

  type TradingApp[A] = EitherK[TradingF, LoggingF, A]

  def composedInterpreter: TradingApp ~> IO = TradingIOInterpreter.step or LoggingIOInterpreter.step

  def program(implicit T: TradingI[TradingApp], L: LoggingI[TradingApp]): Free[TradingApp, List[Trade]] = {
    import T._
    import L._

    for {
      _           <- infoI("Starting trading")
      order       <- fromClientOrderI(cor) 
      _           <- infoI(s"Got order $order")
      executions  <- executeI(m1, ba, order) 
      trades      <- allocateI(List(ca1, ca2, ca3), executions)
      _           <- infoI(s"Got trade $trades")
    } yield trades
  }
  program.foldMap(composedInterpreter).unsafeRunSync
}
