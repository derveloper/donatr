package donatr

import java.nio.charset.StandardCharsets
import java.util.UUID

import com.twitter.finagle.http.Status
import io.finch.{Application, Input}
import org.scalacheck._
import org.scalatest._
import org.scalatest.prop.Checkers

class DonationSpec extends FlatSpec with Matchers with Checkers {
  behavior of "the donations endpoint"

  import DonatrServer._
  import io.circe.generic.auto._
  import io.finch.circe._

  case class DonationWithoutId(from: UUID,
                               to: UUID,
                               value: BigDecimal)

  def genDonationWithoutId: Gen[DonationWithoutId] = for {
    f <- Gen.uuid
    t <- Gen.uuid
    v <- Gen.choose(Double.MinValue, Double.MaxValue)
  } yield DonationWithoutId(f, t, v)

  implicit def arbitraryDonationWithoutId: Arbitrary[DonationWithoutId] = Arbitrary(genDonationWithoutId)

  it should "respond with 404 on random uuid" in {
    val req = Input.get(s"/donations/${UUID.randomUUID().toString}")
    a[EntityNotFound] shouldBe thrownBy(getDonation(req).awaitValueUnsafe())
  }

  it should "create a donation" in {
    check { (donationWithoutId: DonationWithoutId) =>
      val input = Input.post("/donations")
        .withBody[Application.Json](donationWithoutId, Some(StandardCharsets.UTF_8))

      val res = postDonation(input)
      val Some(out) = res.awaitOutputUnsafe()
      out.status === Status.Created
      out.headers contains "Location"
    }
  }

  it should "create two donations" in {
    val first = arbitraryDonationWithoutId.arbitrary.sample.get
    val second = arbitraryDonationWithoutId.arbitrary.sample.get

    val firstInput = Input.post("/donations")
      .withBody[Application.Json](first, Some(StandardCharsets.UTF_8))
    val secondInput = Input.post("/donations")
      .withBody[Application.Json](second, Some(StandardCharsets.UTF_8))

    val Some(firstOutput) = postDonation(firstInput).awaitOutputUnsafe()
    val Some(secondOutput) = postDonation(secondInput).awaitOutputUnsafe()
    firstOutput.headers("Location") shouldBe a[String]
    secondOutput.headers("Location") shouldBe a[String]

    val Some(firstValue) = getDonation(Input.get(firstOutput.headers("Location"))).awaitValueUnsafe()
    val Some(secondValue) = getDonation(Input.get(secondOutput.headers("Location"))).awaitValueUnsafe()
    firstValue shouldBe Donation(firstValue.id, first.from, first.to, first.value)
    secondValue shouldBe Donation(secondValue.id, second.from, second.to, second.value)
  }
}
