package donatr

import java.util.UUID

case class Donater(id: UUID,
                   name: String,
                   email: String,
                   balance: BigDecimal)

case class DonaterWithoutId(name: String,
                            email: String,
                            balance: BigDecimal)

case class Donatable(id: UUID,
                     name: String,
                     minDonationAmount: BigDecimal,
                     balance: BigDecimal)

case class DonatableWithoutId(name: String,
                              minDonationAmount: BigDecimal,
                              balance: BigDecimal)

case class Fundable(id: UUID,
                    name: String,
                    fundingTarget: BigDecimal,
                    balance: BigDecimal = 0)

case class FundableWithoutId(name: String,
                             value: BigDecimal,
                             fundingTarget: BigDecimal,
                             balance: BigDecimal)

case class DonationWithoutId(from: UUID, to: UUID, value: BigDecimal)

case class Donation(id: UUID, from: UUID, to: UUID, value: BigDecimal)
