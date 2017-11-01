package patterns

import java.util.Date

import cats._
import cats.data._
import cats.data.Validated._
import cats.implicits._

trait OrderModel {this: RefModel =>

  case class LineItem(ins: Instrument, qty: BigDecimal, price: BigDecimal)
  case class Order(no: String, date: Date, customer: Customer, items: List[LineItem])

  type ClientOrder = Map[String, String]

  def makeOrder(clientOrder: ClientOrder): Either[DomainValidation, Order] = try {
    val instruments = clientOrder("instrument").split("-")
    val lineItems = instruments map makeLineItem
    Either.right(Order(clientOrder("no"), today, clientOrder("customer"), lineItems.toList))
  } catch {
    case ex: Exception => Either.left(InvalidClientOrder(s"Order $clientOrder is not valid Exception: ${ex.getMessage}"))
  }

  private def makeLineItem(insRecord: String) = { 
    val arr = insRecord.split("/")
    LineItem(arr(0), BigDecimal(arr(1)), BigDecimal(arr(2)))
  }

  def fromClientOrders(clientOrders: List[ClientOrder]): Either[DomainValidation, List[Order]] =
    clientOrders.traverse[Either[DomainValidation, ?], Order](makeOrder)
}
