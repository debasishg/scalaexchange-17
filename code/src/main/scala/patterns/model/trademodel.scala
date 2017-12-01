package patterns
package model

import java.util.{ Date, Calendar }

import cats._
import cats.data._
import cats.implicits._


trait TradeModel {this: RefModel =>

  case class Trade private[patterns] (account: AccountNo, instrument: Instrument, refNo: String, market: Market,
    unitPrice: BigDecimal, quantity: BigDecimal, tradeDate: Date = today,
    valueDate: Option[Date] = None, taxFees: Option[List[(TaxFeeId, BigDecimal)]] = None, 
    netAmount: Option[BigDecimal] = None)

  sealed trait TaxFeeId
  case object TradeTax extends TaxFeeId
  case object Commission extends TaxFeeId
  case object VAT extends TaxFeeId
  case object Surcharge extends TaxFeeId

  // rates of tax/fees expressed as fractions of the principal of the trade
  val rates: Map[TaxFeeId, BigDecimal] = Map(TradeTax -> 0.2, Commission -> 0.15, VAT -> 0.1)

  // tax and fees applicable for each market
  // Other signifies the general rule
  val taxFeeForMarket: Map[Market, List[TaxFeeId]] = 
    Map(Other -> List(TradeTax, Commission), Singapore -> List(TradeTax, Commission, VAT))

  // get the list of tax/fees applicable for this trade
  // depends on the market
  val forTrade: Trade => Option[List[TaxFeeId]] = {trade =>
    taxFeeForMarket.get(trade.market).orElse(taxFeeForMarket.get(Other)) 
  }

  def principal(trade: Trade) = trade.unitPrice * trade.quantity

  // combinator to value a tax/fee for a specific trade
  private val valueAs: Trade => TaxFeeId => BigDecimal = {trade => {tid =>
    ((rates get tid) map (_ * principal(trade))) getOrElse (BigDecimal(0)) }}

  // all tax/fees for a specific trade
  val taxFeeCalculate: Trade => List[TaxFeeId] => List[(TaxFeeId, BigDecimal)] = {t => tids =>
    tids zip (tids map valueAs(t))
  }

  def makeTrade(account: AccountNo, 
    instrument: Instrument, 
    refNo: String, 
    market: Market, 
    unitPrice: BigDecimal, 
    quantity: BigDecimal,
    td: Date = today, 
    vd: Option[Date] = None): Trade = {

    Trade(account, instrument, refNo, market, unitPrice, quantity, td, vd)
  }
}

object TradeModel extends ExecutionModel with OrderModel with RefModel with TradeModel 
