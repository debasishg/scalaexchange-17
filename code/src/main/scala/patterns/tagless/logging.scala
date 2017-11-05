package patterns
package tagless

/**
 *  Use pluggable effects - can work with cats-effect IO - fully RT
 */  
trait Logging[F[_]] {

  def info(msg: String): F[Unit]
  def error(msg: String): F[Unit]
}


