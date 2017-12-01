package patterns
package tagless

import cats.{ Order => OrderZ, _ }
import cats.data._
import cats.implicits._

import model._
import TradeModel._

class AccountRepositoryInterpreter[F[_]](implicit me: MonadError[F, Throwable]) 
  extends AccountRepository[F] {

  val accounts = collection.mutable.HashMap.empty[AccountNo, Account]

  def add(accountNo: AccountNo, name: String): F[Account] = {
    val a = Account(accountNo, name, true)
    accounts += ((accountNo, a))
    a.pure[F]
  }

  def fromNo(accountNo: AccountNo): F[Option[Account]] = {
    accounts.get(accountNo).pure[F]
  }

  def isOpen(accountNo: AccountNo): F[Option[Boolean]] = {
    fromNo(accountNo).map(_.map(_.isActive))
  }
}

