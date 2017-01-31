package donatr

import java.util.UUID

import org.scalacheck.{Arbitrary, Gen}

object Mocks {
  case class DonationWithoutId(from: UUID,
                               to: UUID,
                               value: BigDecimal)

  def genDonationWithoutId: Gen[DonationWithoutId] = for {
    f <- Gen.uuid
    t <- Gen.uuid
    v <- Gen.choose(Double.MinValue, Double.MaxValue)
  } yield DonationWithoutId(f, t, v)

  implicit def arbitraryDonationWithoutId: Arbitrary[DonationWithoutId] = Arbitrary(genDonationWithoutId)

  case class DonatableWithoutId(name: String,
                                minDonationAmount: BigDecimal,
                                balance: BigDecimal)

  def genDonatableWithoutId: Gen[DonatableWithoutId] = for {
    n <- Gen.listOfN(12, Gen.alphaChar).map(_.mkString)
    m <- Gen.choose(Double.MinValue, Double.MaxValue)
    b <- Gen.choose(Double.MinValue, Double.MaxValue)
  } yield DonatableWithoutId(n, m, b)

  implicit def arbitraryDonatableWithoutId: Arbitrary[DonatableWithoutId] = Arbitrary(genDonatableWithoutId)

  case class DonaterWithoutId(name: String,
                              email: String,
                              balance: BigDecimal)

  def genDonaterWithoutId: Gen[DonaterWithoutId] = for {
    n <- Gen.listOfN(12, Gen.alphaChar).map(_.mkString)
    e <- Gen.listOfN(14, Gen.alphaChar).map(_.mkString)
    b <- Gen.choose(Double.MinValue, Double.MaxValue)
  } yield DonaterWithoutId(n, e, b)

  implicit def arbitraryDonaterWithoutId: Arbitrary[DonaterWithoutId] = Arbitrary(genDonaterWithoutId)

  case class FundableWithoutId(name: String,
                               fundingTarget: BigDecimal,
                               balance: BigDecimal)

  def genFundableWithoutId: Gen[FundableWithoutId] = for {
    n <- Gen.listOfN(12, Gen.alphaChar).map(_.mkString)
    m <- Gen.choose(Double.MinValue, Double.MaxValue)
    b <- Gen.choose(Double.MinValue, Double.MaxValue)
  } yield FundableWithoutId(n, m, b)

  implicit def arbitraryFundableWithoutId: Arbitrary[FundableWithoutId] = Arbitrary(genFundableWithoutId)
}
