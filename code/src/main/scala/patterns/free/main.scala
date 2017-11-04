package patterns
package free

import cats.{ Order => OrderZ, _ }
import cats.data._
import cats.implicits._
import cats.effect.IO

import model._
import TradeModel._

object Main {
  import TradingService._

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

  val tradeGen: Kleisli[Trading, ClientOrder, List[Trade]] = tradeGeneration(m1, ba, List(ca1, ca2, ca3))

  val ioTrades: IO[List[Trade]] = new TradingIOInterpreter().apply(tradeGen(cor))
  ioTrades.unsafeRunSync()
}

