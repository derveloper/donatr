package donatr

import java.nio.charset.StandardCharsets
import java.util.UUID

import com.twitter.finagle.http.Status
import io.finch.{Application, Input}
import org.scalacheck._
import org.scalatest._
import org.scalatest.prop.Checkers

class DonatersSpec extends FlatSpec with Matchers with Checkers{
  behavior of "the donaters endpoint"

  import DonatrServer._
  import io.circe.generic.auto._
  import io.finch.circe._
  import Mocks._

  it should "respond with 404 on random uuid" in {
    val req = Input.get(s"/donaters/${UUID.randomUUID().toString}")
    a[EntityNotFound] shouldBe thrownBy(getDonater(req).awaitValueUnsafe())
  }

  it should "create a donater" in {
    check { (donaterWithoutId: Mocks.DonaterWithoutId) =>
      val input = Input.post("/donaters")
        .withBody[Application.Json](donaterWithoutId, Some(StandardCharsets.UTF_8))

      val res = postDonater(input)
      val Some(out) = res.awaitOutputUnsafe()
      out.status === Status.Created
      out.headers contains "Location"
    }
  }

  it should "create a donater and duplicate should return 400" in {
    val donaterWithoutId = arbitraryDonaterWithoutId.arbitrary.sample.get
    val input = Input.post("/donaters")
      .withBody[Application.Json](donaterWithoutId, Some(StandardCharsets.UTF_8))

    postDonater(input).awaitOutputUnsafe().map(_.headers).map(_ ("Location")).get shouldBe a[String]
    a[NameTaken] shouldBe thrownBy(postDonater(input).awaitValueUnsafe())
  }

  it should "create two donaters" in {
    val first = arbitraryDonaterWithoutId.arbitrary.sample.get
    val second = arbitraryDonaterWithoutId.arbitrary.sample.get

    val firstInput = Input.post("/donaters")
      .withBody[Application.Json](first, Some(StandardCharsets.UTF_8))
    val secondInput = Input.post("/donaters")
      .withBody[Application.Json](second, Some(StandardCharsets.UTF_8))

    val Some(firstOutput) = postDonater(firstInput).awaitOutputUnsafe()
    val Some(secondOutput) = postDonater(secondInput).awaitOutputUnsafe()
    firstOutput.headers("Location") shouldBe a[String]
    secondOutput.headers("Location") shouldBe a[String]

    val Some(firstValue) = getDonater(Input.get(firstOutput.headers("Location"))).awaitValueUnsafe()
    val Some(secondValue) = getDonater(Input.get(secondOutput.headers("Location"))).awaitValueUnsafe()
    firstValue shouldBe Donater(firstValue.id, first.name, first.email, first.balance)
    secondValue shouldBe Donater(secondValue.id, second.name, second.email, second.balance)
  }
}
