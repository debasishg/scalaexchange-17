package patterns
package tagless

import cats.{ Order => OrderZ, _ }
import cats.data._
import cats.implicits._
import cats.effect.IO

import model._
import TradeModel._

object Main {

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

  object TradingComponent extends TradingInterpreter[IO]
  object LoggingComponent extends LoggingInterpreter[IO]

  import TradingComponent._
  import LoggingComponent._

  val ioTrades: IO[List[Trade]] = for {
    _           <- info("Starting trading")
    order       <- fromClientOrder(cor) 
    _           <- info(s"Got order $order")
    executions  <- execute(m1, ba, order) 
    trades      <- allocate(List(ca1, ca2, ca3), executions)
    _           <- info(s"Got trade $trades")
  } yield trades

  ioTrades.unsafeRunSync()
}

