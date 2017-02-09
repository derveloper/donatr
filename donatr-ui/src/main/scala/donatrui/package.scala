package object donatrui {
  def md5(string: String) = {
    import scala.scalajs.js
    val spark = js.Dynamic.newInstance(js.Dynamic.global.SparkMD5)()
    spark.append(string)
    spark.end().asInstanceOf[String]
  }
}
