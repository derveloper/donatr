package donatrui

import org.scalatest.selenium.Chrome
import org.scalatest.{FlatSpec, Matchers}

class DonatrUiSpec extends FlatSpec with Matchers with Chrome {
  val host = "http://localhost:8080/"

  "The app home page" should "have the correct title" in {
    go to host
    pageTitle should be ("donatr-ui")
  }

  "The app home page" should "have a +user button" in {
    go to host
    click on linkText("+user")
    click on linkText("Create")
  }
}
