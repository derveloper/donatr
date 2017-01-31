package donatr

import java.nio.charset.StandardCharsets
import java.util.UUID

import io.finch.Application.Json
import org.scalatest._
import org.scalatest.prop.Checkers

class DonationSpec extends FlatSpec with Matchers with Checkers {
  behavior of "the donations endpoint"

  import DonatrServer._
  import Mocks._
  import io.circe.generic.auto._
  import io.finch.circe._
  import io.finch.{Input, Output}

  it should "respond with 404 on random uuid" in {
    val req = Input.get(s"/donations/${UUID.randomUUID().toString}")
    a[EntityNotFound] shouldBe thrownBy(getDonation(req).awaitValueUnsafe())
  }

  it should "create donater and donatable and two donations and have correct balances afterwards" in {
    val (donatable, donatableOutput) = mkDonatable
    val (donater, donaterOutput) = mkDonater

    val (donatableValue: Donatable, donaterValue: Donater) =
      getDonatableAndDonater(donatable, donatableOutput, donater, donaterOutput)

    val (first, second) = mkDonation(donaterValue.id, donatableValue.id)

    assertDonations(donatable, donatableOutput, donater, donaterOutput, first, second)
  }

  it should "create donater and donater and two donations and have correct balances afterwards" in {
    val (donater1, donaterOutput1) = mkDonater
    val (donater2, donaterOutput2) = mkDonater

    val donaterValue1: Donater = fetchDonater(donater1, donaterOutput1)
    val donaterValue2: Donater = fetchDonater(donater2, donaterOutput2)

    val (first, second) = mkDonation(donaterValue2.id, donaterValue1.id)

    assertDonations(donater1, donaterOutput1, donater2, donaterOutput2, first, second)
  }

  it should "create donater and fundable and two donations and have correct balances afterwards" in {
    val (donater, donaterOutput) = mkDonater
    val (fundable, fundableOutput) = mkFundable

    val donaterValue: Donater = fetchDonater(donater, donaterOutput)
    val fundableValue: Fundable = fetchFundable(fundable, fundableOutput)

    val (first, second) = mkDonation(donaterValue.id, fundableValue.id)

    assertDonations(fundable, fundableOutput, donater, donaterOutput, first, second)
  }

  private def getDonatableAndDonater(donatable: Mocks.DonatableWithoutId, donatableOutput: Output[Unit],
                                     donater: Mocks.DonaterWithoutId, donaterOutput: Output[Unit]) = {
    val donatableValue: Donatable = fetchDonatable(donatable, donatableOutput)
    val donaterValue: Donater = fetchDonater(donater, donaterOutput)
    (donatableValue, donaterValue)
  }

  private def fetchDonater(donater: Mocks.DonaterWithoutId, donaterOutput: Output[Unit]) = {
    val Some(donaterValue) = getDonater(Input.get(donaterOutput.headers("Location"))).awaitValueUnsafe()
    donaterValue shouldBe Donater(donaterValue.id, donater.name, donater.email, donater.balance)
    donaterValue
  }

  private def fetchFundable(fundable: Mocks.FundableWithoutId, fundableOutput: Output[Unit]) = {
    val Some(fundableValue) = getFundable(Input.get(fundableOutput.headers("Location"))).awaitValueUnsafe()
    fundableValue shouldBe Fundable(fundableValue.id, fundable.name, fundable.fundingTarget, fundable.balance)
    fundableValue
  }

  private def fetchDonatable(donatable: Mocks.DonatableWithoutId, donatableOutput: Output[Unit]) = {
    val Some(donatableValue) = getDonatable(Input.get(donatableOutput.headers("Location"))).awaitValueUnsafe()
    donatableValue shouldBe Donatable(donatableValue.id, donatable.name, donatable.minDonationAmount, donatable.balance)
    donatableValue
  }

  private def assertDonations(donatable: Mocks.DonatableWithoutId, donatableOutput: Output[Unit],
                              donater: Mocks.DonaterWithoutId, donaterOutput: Output[Unit],
                              first: Mocks.DonationWithoutId, second: Mocks.DonationWithoutId) = {
    val Some(donatableValue2) = getDonatable(Input.get(donatableOutput.headers("Location"))).awaitValueUnsafe()
    donatableValue2 shouldBe Donatable(donatableValue2.id, donatable.name,
      donatable.minDonationAmount, donatable.balance + first.value + second.value)

    val Some(donaterValue2) = getDonater(Input.get(donaterOutput.headers("Location"))).awaitValueUnsafe()
    donaterValue2 shouldBe Donater(donaterValue2.id, donater.name, donater.email,
      donater.balance - first.value - second.value)
  }

  private def assertDonations(fundable: Mocks.FundableWithoutId, fundableOutput: Output[Unit],
                              donater: Mocks.DonaterWithoutId, donaterOutput: Output[Unit],
                              first: Mocks.DonationWithoutId, second: Mocks.DonationWithoutId) = {
    val Some(fundableValue2) = getFundable(Input.get(fundableOutput.headers("Location"))).awaitValueUnsafe()
    fundableValue2 shouldBe Fundable(fundableValue2.id, fundable.name,
      fundable.fundingTarget, fundable.balance + first.value + second.value)

    val Some(donaterValue2) = getDonater(Input.get(donaterOutput.headers("Location"))).awaitValueUnsafe()
    donaterValue2 shouldBe Donater(donaterValue2.id, donater.name, donater.email,
      donater.balance - first.value - second.value)
  }

  private def assertDonations(donater1: Mocks.DonaterWithoutId, donaterOutput1: Output[Unit],
                              donater2: Mocks.DonaterWithoutId, donaterOutput2: Output[Unit],
                              first: Mocks.DonationWithoutId, second: Mocks.DonationWithoutId) = {
    val Some(donaterValue1) = getDonater(Input.get(donaterOutput1.headers("Location"))).awaitValueUnsafe()
    donaterValue1 shouldBe Donater(donaterValue1.id, donater1.name, donater1.email,
      donater1.balance + first.value + second.value)

    val Some(donaterValue2) = getDonater(Input.get(donaterOutput2.headers("Location"))).awaitValueUnsafe()
    donaterValue2 shouldBe Donater(donaterValue2.id, donater2.name, donater2.email,
      donater2.balance - first.value - second.value)
  }

  private def mkDonater = {
    val donater = arbitraryDonaterWithoutId.arbitrary.sample.get
    val donaterInput = Input.post("/donaters")
      .withBody[Json](donater, Some(StandardCharsets.UTF_8))

    val Some(donaterOutput) = postDonater(donaterInput).awaitOutputUnsafe()
    donaterOutput.headers("Location") shouldBe a[String]

    (donater, donaterOutput)
  }

  private def mkDonatable = {
    val donatable = arbitraryDonatableWithoutId.arbitrary.sample.get
    val donatableInput = Input.post("/donatables")
      .withBody[Json](donatable, Some(StandardCharsets.UTF_8))

    val Some(donatableOutput) = postDonatable(donatableInput).awaitOutputUnsafe()
    donatableOutput.headers("Location") shouldBe a[String]

    (donatable, donatableOutput)
  }

  private def mkFundable = {
    val fundable = arbitraryFundableWithoutId.arbitrary.sample.get
    val fundableInput = Input.post("/fundables")
      .withBody[Json](fundable, Some(StandardCharsets.UTF_8))

    val Some(fundableOutput) = postFundable(fundableInput).awaitOutputUnsafe()

    fundableOutput.headers("Location") shouldBe a[String]
    (fundable, fundableOutput)
  }

  private def mkDonation(from: UUID, to: UUID) = {
    val first = arbitraryDonationWithoutId.arbitrary.sample.get.copy(from = from, to = to)
    val second = arbitraryDonationWithoutId.arbitrary.sample.get.copy(from = from, to = to)

    val firstInput = Input.post("/donations")
      .withBody[Json](first, Some(StandardCharsets.UTF_8))
    val secondInput = Input.post("/donations")
      .withBody[Json](second, Some(StandardCharsets.UTF_8))

    val Some(firstOutput) = postDonation(firstInput).awaitOutputUnsafe()
    val Some(secondOutput) = postDonation(secondInput).awaitOutputUnsafe()
    firstOutput.headers("Location") shouldBe a[String]
    secondOutput.headers("Location") shouldBe a[String]
    (first, second)
  }
}
