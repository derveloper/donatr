import org.scalajs.dom.Event
import org.scalajs.dom.raw.HTMLInputElement

import scala.scalajs.js
import scala.scalajs.js.timers.SetTimeoutHandle

package object donatrui {
  def md5(string: String): String = {
    import scala.scalajs.js
    val spark = js.Dynamic.newInstance(js.Dynamic.global.SparkMD5)()
    spark.append(string)
    spark.end().asInstanceOf[String]
  }

  def inputEvent(f: HTMLInputElement => Unit): Event => Unit = {
    event: Event =>
      event.target match {
        case e: HTMLInputElement =>
          f(e)
        case _ =>
      }
  }

  object debounce {
    var timeoutHandler: js.UndefOr[SetTimeoutHandle] = js.undefined
    def apply[A, B](timeout: Double)(f: A => B): A => Unit = { a =>
      timeoutHandler foreach js.timers.clearTimeout
      timeoutHandler = js.timers.setTimeout(timeout) {
        f(a)
        ()
      }
    }
  }
}
