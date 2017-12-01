package patterns
package tagless.kleisli

import cats.{ Order => OrderZ, _ }
import cats.data._
import cats.implicits._
import cats.effect.IO

import model._
import TradeModel._

object Main {

  trait Data {
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
  }

  /*
  object TradeGenerationIO extends Data {
    def tradeGeneration[M[_]: FlatMap](T: Trading[M]): Kleisli[M, ClientOrder, List[Trade]] = 
      T.fromClientOrder andThen T.execute(m1, ba) andThen T.allocate(List(ca1, ca2, ca3))

    object TradingComponent extends TradingInterpreter[IO]
    val tk = tradeGeneration(TradingComponent)

    tk(cor).unsafeRunSync
  }
  */
}
