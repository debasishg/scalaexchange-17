package patterns

import cats._
import cats.data._
import cats.implicits._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 *  Plain monadic disjunction
 */  
trait TradingA[Account, Trade, ClientOrder, Order, Execution, Market] {

  def fromClientOrder: ClientOrder => Either[DomainValidation, Order]
  def execute(market: Market, brokerAccount: Account): Order => Either[DomainValidation, List[Execution]]
  def allocate(accounts: List[Account]): List[Execution] => Either[DomainValidation, List[Trade]]

  def tradeGeneration(
    market: Market, 
    broker: Account, 
    clientAccounts: List[Account])(clientOrder: ClientOrder): Either[DomainValidation, List[Trade]] = for {
      order        <- fromClientOrder(clientOrder)
      executions   <- execute(market, broker)(order)
      trades       <- allocate(clientAccounts)(executions)
    } yield trades

  def fromClientOrders(
    market: Market, 
    broker: Account, 
    clientAccounts: List[Account], 
    clientOrders: List[ClientOrder]) = 
    clientOrders.traverse[Either[DomainValidation, ?], List[Trade]](tradeGeneration(market, broker, clientAccounts))
}

/**
 * Monadic disjunction with ReaderT
 */ 
trait TradingB[Account, Trade, ClientOrder, Order, Execution, Market] {

  def fromClientOrder: Kleisli[Either[DomainValidation, ?], ClientOrder, Order]
  def execute(market: Market, brokerAccount: Account): Kleisli[Either[DomainValidation, ?], Order, List[Execution]]
  def allocate(accounts: List[Account]): Kleisli[Either[DomainValidation, ?], List[Execution], List[Trade]]

  def tradeGeneration(
    market: Market, 
    broker: Account, 
    clientAccounts: List[Account])(clientOrder: ClientOrder): Kleisli[Either[DomainValidation, ?], ClientOrder, List[Trade]] = 
    fromClientOrder andThen execute(market, broker) andThen allocate(clientAccounts)

  type DomainValidationResult[A] = Either[DomainValidation, A]
  type ReadAndValidate[A] = Kleisli[DomainValidationResult, ClientOrder, A]

  def fromClientOrders(
    market: Market, 
    broker: Account, 
    clientAccounts: List[Account], 
    clientOrders: List[ClientOrder]) = clientOrders.traverse[ReadAndValidate, List[Trade]](tradeGeneration(market, broker, clientAccounts))
}

/**
 * Stack in yet another monad - Future
 */ 
trait TradingC[Account, Trade, ClientOrder, Order, Execution, Market] {

  def fromClientOrder: Kleisli[EitherT[Future, DomainValidation, ?], ClientOrder, Order]
  def execute(market: Market, brokerAccount: Account): Kleisli[EitherT[Future, DomainValidation, ?], Order, List[Execution]]
  def allocate(accounts: List[Account]): Kleisli[EitherT[Future, DomainValidation, ?], List[Execution], List[Trade]]

  def tradeGeneration(
    market: Market, 
    broker: Account, 
    clientAccounts: List[Account])(clientOrder: ClientOrder): Kleisli[EitherT[Future, DomainValidation, ?], ClientOrder, List[Trade]] = 
    fromClientOrder andThen execute(market, broker) andThen allocate(clientAccounts)

  type DomainValidationResult[A] = EitherT[Future, DomainValidation, A]
  type ReadAndValidate[A] = Kleisli[DomainValidationResult, ClientOrder, A]

  def fromClientOrders(
    market: Market, 
    broker: Account, 
    clientAccounts: List[Account], 
    clientOrders: List[ClientOrder]) = clientOrders.traverse[ReadAndValidate, List[Trade]](tradeGeneration(market, broker, clientAccounts))
}
