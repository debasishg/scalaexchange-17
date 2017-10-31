package patterns

sealed trait DomainValidation {
  def errorMessage: String
}

case class InvalidClientOrder(errorMessage: String) extends DomainValidation
