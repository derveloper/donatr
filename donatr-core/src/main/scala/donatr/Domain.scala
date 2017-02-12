package donatr

import java.util.UUID

case class Ledger(id: UUID,
                  balance: BigDecimal = 0)

case class Donater(id: UUID,
                   name: String,
                   email: String,
                   balance: BigDecimal)

case class DonaterWithoutId(name: String,
                            email: String,
                            balance: BigDecimal)

case class Donatable(id: UUID,
                     name: String,
                     imageUrl: String,
                     minDonationAmount: BigDecimal,
                     balance: BigDecimal)

case class DonatableWithoutId(name: String,
                              imageUrl: String,
                              minDonationAmount: BigDecimal,
                              balance: BigDecimal)

case class Fundable(id: UUID,
                    name: String,
                    imageUrl: String,
                    fundingTarget: BigDecimal,
                    balance: BigDecimal = 0)

case class FundableWithoutId(name: String,
                             imageUrl: String,
                             fundingTarget: BigDecimal,
                             balance: BigDecimal)

case class DonationWithoutId(from: UUID, to: UUID, value: BigDecimal)
case class DonationWithoutIdAndFrom(to: UUID, value: BigDecimal)

case class Donation(id: UUID, from: UUID, to: UUID, value: BigDecimal)