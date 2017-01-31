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

  case class DonaterWithoutId(name: String,
                              email: String,
                              balance: BigDecimal)

  def genDonaterWithoutId: Gen[DonaterWithoutId] = for {
    n <- Gen.listOfN(12, Gen.alphaChar).map(_.mkString)
    e <- Gen.listOfN(14, Gen.alphaChar).map(_.mkString)
    b <- Gen.choose(Double.MinValue, Double.MaxValue)
  } yield DonaterWithoutId(n, e, b)

  implicit def arbitraryDonaterWithoutId: Arbitrary[DonaterWithoutId] = Arbitrary(genDonaterWithoutId)

  it should "respond with 404 on random uuid" in {
    val req = Input.get(s"/donaters/${UUID.randomUUID().toString}")
    a[DonatrNotFound] shouldBe thrownBy(getDonater(req).awaitValueUnsafe())
  }

  it should "create a donater" in {
    check { (donaterWithoutId: DonaterWithoutId) =>
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
    val donaterWithoutId1 = arbitraryDonaterWithoutId.arbitrary.sample.get
    val donaterWithoutId2 = arbitraryDonaterWithoutId.arbitrary.sample.get
    val input1 = Input.post("/donaters")
      .withBody[Application.Json](donaterWithoutId1, Some(StandardCharsets.UTF_8))
    val input2 = Input.post("/donaters")
      .withBody[Application.Json](donaterWithoutId2, Some(StandardCharsets.UTF_8))

    val Some(value1) = postDonater(input1).awaitOutputUnsafe()
    val Some(value2) = postDonater(input2).awaitOutputUnsafe()
    value1.headers("Location") shouldBe a[String]
    value2.headers("Location") shouldBe a[String]
    getDonater(Input.get(value1.headers("Location"))).awaitValueUnsafe() shouldBe Some(donaterWithoutId1)
  }
}
