package patterns
package model

sealed trait DomainValidation {
  def message: String
}

case class InvalidClientOrder(message: String) extends DomainValidation
case class DomainSuccess(message: String) extends DomainValidation
