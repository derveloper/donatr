package donatr

import java.nio.charset.StandardCharsets
import java.util.UUID

import com.twitter.finagle.http.Status
import io.finch.{Application, Input}
import org.scalacheck._
import org.scalatest._
import org.scalatest.prop.Checkers

class DonatableSpec extends FlatSpec with Matchers with Checkers{
  behavior of "the donatables endpoint"

  import DonatrServer._
  import io.circe.generic.auto._
  import io.finch.circe._
  import Mocks._

  it should "respond with 404 on random uuid" in {
    val req = Input.get(s"/donatables/${UUID.randomUUID().toString}")
    a[EntityNotFound] shouldBe thrownBy(getDonatable(req).awaitValueUnsafe())
  }

  it should "create a donatable" in {
    check { (donatableWithoutId: Mocks.DonatableWithoutId) =>
      val input = Input.post("/donatables")
        .withBody[Application.Json](donatableWithoutId, Some(StandardCharsets.UTF_8))

      val res = postDonatable(input)
      val Some(out) = res.awaitOutputUnsafe()
      out.status === Status.Created
      out.headers contains "Location"
    }
  }

  it should "create a donatable and duplicate should return 400" in {
    val donatableWithoutId = arbitraryDonatableWithoutId.arbitrary.sample.get
    val input = Input.post("/donatables")
      .withBody[Application.Json](donatableWithoutId, Some(StandardCharsets.UTF_8))

    postDonatable(input).awaitOutputUnsafe().map(_.headers).map(_ ("Location")).get shouldBe a[String]
    a[NameTaken] shouldBe thrownBy(postDonatable(input).awaitValueUnsafe())
  }

  it should "create two donatables" in {
    val first = arbitraryDonatableWithoutId.arbitrary.sample.get
    val second = arbitraryDonatableWithoutId.arbitrary.sample.get

    val firstInput = Input.post("/donatables")
      .withBody[Application.Json](first, Some(StandardCharsets.UTF_8))
    val secondInput = Input.post("/donatables")
      .withBody[Application.Json](second, Some(StandardCharsets.UTF_8))

    val Some(firstOutput) = postDonatable(firstInput).awaitOutputUnsafe()
    val Some(secondOutput) = postDonatable(secondInput).awaitOutputUnsafe()
    firstOutput.headers("Location") shouldBe a[String]
    secondOutput.headers("Location") shouldBe a[String]

    val Some(firstValue) = getDonatable(Input.get(firstOutput.headers("Location"))).awaitValueUnsafe()
    val Some(secondValue) = getDonatable(Input.get(secondOutput.headers("Location"))).awaitValueUnsafe()
    firstValue shouldBe Donatable(firstValue.id, first.name, first.minDonationAmount, first.balance)
    secondValue shouldBe Donatable(secondValue.id, second.name, second.minDonationAmount, second.balance)
  }
}
