package patterns
package tagless

import model._
import TradeModel._

trait AccountRepository[F[_]] {
  def add(accountNo: AccountNo, name: String): F[Account]
  def fromNo(accountNo: AccountNo): F[Option[Account]]
  def isOpen(accountNo: AccountNo): F[Option[Boolean]]
}
