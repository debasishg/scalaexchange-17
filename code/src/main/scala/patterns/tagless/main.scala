package patterns
package tagless

import cats.{ Order => OrderZ, _ }
import cats.data._
import cats.implicits._
import cats.effect.IO

import model._
import TradeModel._

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

object TradeGenerationIO extends Data {
  def tradeGeneration[M[_]: Monad](T: Trading[M], A: AccountRepository[M], brokerAccountNo: String)(implicit me: MonadError[M, Throwable]) = for {
    order                 <- T.fromClientOrder(cor) 
    maybeBrokerAccount    <- A.fromNo(brokerAccountNo)
    executions            <- maybeBrokerAccount.map(b => T.execute(m1, b, order))
                                               .getOrElse(me.raiseError(new Exception(s"Invalid account number $brokerAccountNo")))
    trades                <- T.allocate(List(ca1, ca2, ca3), executions)
  } yield trades

  object TradingComponent extends TradingInterpreter[IO]
  object AccountRepositoryComponent extends AccountRepositoryInterpreter[IO]
  tradeGeneration(TradingComponent, AccountRepositoryComponent, ba).unsafeRunSync
}

object TradeGenerationMonix extends Data {
  import monix.eval.Task

  def tradeGeneration[M[_]: FlatMap](T: Trading[M], A: AccountRepository[M])(implicit me: MonadError[M, Throwable]) = for {
    order                 <- T.fromClientOrder(cor) 
    maybeBrokerAccount    <- A.fromNo(ba)
    executions            <- maybeBrokerAccount.map(b => T.execute(m1, b, order))
                                               .getOrElse(me.raiseError(new Exception(s"Invalid account number $ba")))
    trades                <- T.allocate(List(ca1, ca2, ca3), executions)
  } yield trades

  object TradingComponent extends TradingInterpreter[Task]
  object AccountRepositoryComponent extends AccountRepositoryInterpreter[Task]
  tradeGeneration(TradingComponent, AccountRepositoryComponent)
}

object TradeGenerationLoggable extends Data {
  def tradeGenerationLoggable[F[_]: FlatMap](T: Trading[F], L: Logging[F], A: AccountRepository[F])(implicit me: MonadError[F, Throwable]) = for {
    _                     <- L.info("starting order processing")
    order                 <- T.fromClientOrder(cor) 
    maybeBrokerAccount    <- A.fromNo(ba)
    executions            <- maybeBrokerAccount.map(b => T.execute(m1, b, order))
                                               .getOrElse(me.raiseError(new Exception(s"Invalid account number $ba")))
    trades                <- T.allocate(List(ca1, ca2, ca3), executions)
    _                     <- L.info("allocation done")
  } yield trades

  object TradingComponent extends TradingInterpreter[IO]
  object LoggingComponent extends LoggingInterpreter[IO]
  object AccountRepositoryComponent extends AccountRepositoryInterpreter[IO]

  tradeGenerationLoggable(TradingComponent, LoggingComponent, AccountRepositoryComponent).unsafeRunSync
}

/*
object TradeGenerationAuditable extends Data {
  import scala.concurrent.Future
  import scala.concurrent.ExecutionContext.Implicits.global

  def tradeGenerationAuditable[F[_]: FlatMap](T: AuditableTrading[F], A: AccountRepository[M]) = for {
    order       <- T.fromClientOrder(cor) 
    executions  <- T.execute(m1, ba, order, A) 
    trades      <- T.allocate(List(ca1, ca2, ca3), executions)
  } yield trades

  object TradingComponentF extends TradingInterpreter[Future]
  tradeGenerationAuditable(new AuditableTrading[Future](TradingComponentF))
}
*/

