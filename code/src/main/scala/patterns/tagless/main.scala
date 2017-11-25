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

  def tradeGeneration[F[_]](T: Trading[F], L: Logging[F])(implicit me: MonadError[F, Throwable]) = for {
    _           <- L.info("starting order processing")
    order       <- T.fromClientOrder(cor) 
    executions  <- T.execute(m1, ba)(order) 
    trades      <- T.allocate(List(ca1, ca2, ca3))(executions)
    _           <- L.info("allocation done")
  } yield trades

  object TradingComponent extends TradingInterpreter[IO]
  object LoggingComponent extends LoggingInterpreter[IO]

  tradeGeneration(TradingComponent, LoggingComponent).unsafeRunSync
}

