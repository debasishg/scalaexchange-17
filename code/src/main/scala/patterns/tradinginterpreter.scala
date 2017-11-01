package patterns

import java.util.{ Date, Calendar }

import cats.{ Order => OrderZ, _ }
import cats.data._
import cats.implicits._
import cats.effect.IO

import TradeModel._

trait TradingInterpreter extends TradingD[IO, Account, Trade, ClientOrder, Order, Execution, Market] {

  def fromClientOrder = Kleisli[EitherT[IO, DomainValidation, ?], ClientOrder, Order] { clientOrder =>
    EitherT {
      IO { makeOrder(clientOrder) }
    }
  }

  def execute(market: Market, brokerAccount: Account) = Kleisli[EitherT[IO, DomainValidation, ?], Order, List[Execution]] { order =>
    EitherT {
      IO { 
        Either.right {
          order.items.map { item =>
            Execution(brokerAccount, item.ins, "e-123", market, item.price, item.qty)
          }
        }
      }
    }
  }

  def allocate(accounts: List[Account]) = Kleisli[EitherT[IO, DomainValidation, ?], List[Execution], List[Trade]] { executions =>
    EitherT {
      IO { 
        Either.right {
          executions.map { execution =>
            val q = execution.quantity / accounts.size
            accounts.map { account =>
              makeTrade(account, execution.instrument, "t-123", execution.market, execution.unitPrice, q)
            }
          }.flatten
        }
      }
    }
  }
}

object TradingInterpreter extends TradingInterpreter

