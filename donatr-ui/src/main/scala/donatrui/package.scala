import org.scalajs.dom.Event
import org.scalajs.dom.raw.HTMLInputElement

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
}
