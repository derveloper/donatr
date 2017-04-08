package donatrui

import mhtml.{Rx, Var}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

object Utils {
  def fromFuture[T](future: Future[T])(implicit ec: ExecutionContext): Rx[Option[Try[T]]] = {
    val result = Var(Option.empty[Try[T]])
    future.onComplete(x => result := Some(x))
    result
  }
}
