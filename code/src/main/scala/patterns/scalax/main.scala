package patterns
package scalax

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

  {
    object TradingComponent extends TradingInterpreter[IO]
  
    import TradingComponent._
  
    val ioTrades: IO[List[Trade]] = for {
      order       <- fromClientOrder(cor) 
      executions  <- execute(m1, ba)(order) 
      trades      <- allocate(List(ca1, ca2, ca3))(executions)
    } yield trades
  
    ioTrades.unsafeRunSync()
  }

  {
    object TradingComponentK extends TradingInterpreterK[IO]

    import TradingComponentK._

    val kTrades: Kleisli[IO, ClientOrder, List[Trade]] = fromClientOrder andThen execute(m2, ba) andThen allocate(List(ca1, ca2, ca3))
    kTrades(cor).unsafeRunSync()
  }
}

