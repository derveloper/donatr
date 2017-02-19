package donatr

object DonatrTypes {
  import java.util.UUID
  type Id = UUID
  type Name = String
  type Email = String
  type ImageUrl = String
  type Balance = BigDecimal
  type MinDonation = BigDecimal
  type FundingTarget = BigDecimal
  type Value = BigDecimal
}
