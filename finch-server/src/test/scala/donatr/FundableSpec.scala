package donatr

import java.nio.charset.StandardCharsets
import java.util.UUID

import com.twitter.finagle.http.Status
import io.finch.{Application, Input}
import org.scalatest._
import org.scalatest.prop.Checkers

class FundableSpec extends FlatSpec with Matchers with Checkers {
  behavior of "the fundables endpoint"

  import DonatrServer._
  import Mocks._
  import io.circe.generic.auto._
  import io.finch.circe._

  it should "respond with 404 on random uuid" in {
    val req = Input.get(s"/fundables/${UUID.randomUUID().toString}")
    a[EntityNotFound] shouldBe thrownBy(getFundable(req).awaitValueUnsafe())
  }

  it should "create a fundable" in {
    check { (fundableWithoutId: Mocks.FundableWithoutId) =>
      val input = Input.post("/fundables")
        .withBody[Application.Json](fundableWithoutId, Some(StandardCharsets.UTF_8))

      val res = postFundable(input)
      val Some(out) = res.awaitOutputUnsafe()
      out.status === Status.Created
      out.headers contains "Location"
    }
  }

  it should "create a fundable and duplicate should return 400" in {
    val fundableWithoutId = arbitraryFundableWithoutId.arbitrary.sample.get
    val input = Input.post("/fundables")
      .withBody[Application.Json](fundableWithoutId, Some(StandardCharsets.UTF_8))

    postFundable(input).awaitOutputUnsafe().map(_.headers).map(_ ("Location")).get shouldBe a[String]
    a[NameTaken] shouldBe thrownBy(postFundable(input).awaitValueUnsafe())
  }

  it should "create two fundables" in {
    val first = arbitraryFundableWithoutId.arbitrary.sample.get
    val second = arbitraryFundableWithoutId.arbitrary.sample.get

    val firstInput = Input.post("/fundables")
      .withBody[Application.Json](first, Some(StandardCharsets.UTF_8))
    val secondInput = Input.post("/fundables")
      .withBody[Application.Json](second, Some(StandardCharsets.UTF_8))

    val Some(firstOutput) = postFundable(firstInput).awaitOutputUnsafe()
    val Some(secondOutput) = postFundable(secondInput).awaitOutputUnsafe()
    firstOutput.headers("Location") shouldBe a[String]
    secondOutput.headers("Location") shouldBe a[String]

    val Some(firstValue) = getFundable(Input.get(firstOutput.headers("Location"))).awaitValueUnsafe()
    val Some(secondValue) = getFundable(Input.get(secondOutput.headers("Location"))).awaitValueUnsafe()
    firstValue shouldBe Fundable(firstValue.id, first.name, first.fundingTarget, first.balance)
    secondValue shouldBe Fundable(secondValue.id, second.name, second.fundingTarget, second.balance)
  }
}
