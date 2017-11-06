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

  val ba1 = "broker-account-1"
  val ba2 = "broker-account-2"

  val ca1 = "client-account-1"
  val ca2 = "client-account-2"
  val ca3 = "client-account-3"
  val ca4 = "client-account-4"
  val ca5 = "client-account-5"

  val ti = new TradingIOInterpreter()

  val cor1 = Map(
    "no" -> "client-order-1",
    "customer" -> "customer-1",
    "instrument" -> "ibm/100/1000-google/200/2000"
  )

  val tradeGen1: Kleisli[Trading, ClientOrder, List[Trade]] = tradeGeneration(m1, ba1, List(ca1, ca2, ca3))

  val ioTrades1: IO[List[Trade]] = ti.apply(tradeGen1(cor1))

  val cor2 = Map(
    "no" -> "client-order-2",
    "customer" -> "customer-2",
    "instrument" -> "ibm/100/1000-google/200/2000"
  )

  val tradeGen2: Kleisli[Trading, ClientOrder, List[Trade]] = tradeGeneration(m2, ba2, List(ca4, ca5))

  val ioTrades2: IO[List[Trade]] = ti.apply(tradeGen2(cor2))

  ioTrades1.unsafeRunSync()

  def runAsync1: IO[Unit] = ioTrades1.runAsync { 
    case Left(ex) => IO(ex.printStackTrace)
    case Right(ts) => IO(ts foreach println)
  }

  def runAsync2: IO[Unit] = ioTrades2.runAsync { 
    case Left(ex) => IO(ex.printStackTrace)
    case Right(ts) => IO(ts foreach println)
  }

  for {
    ts1 <- runAsync1
    ts2 <- runAsync2
  } yield ()
}

