package donatr

import java.util.UUID

class DonationReceiver(val id: UUID)

case class Donater(override val id: UUID,
                   name: String,
                   email: String,
                   balance: BigDecimal) extends DonationReceiver(id)

case class DonaterWithoutId(name: String,
                            email: String,
                            balance: BigDecimal)

case class Donatable(override val id: UUID,
                     name: String,
                     minDonationAmount: BigDecimal,
                     balance: BigDecimal) extends DonationReceiver(id)

case class DonatableWithoutId(name: String,
                              minDonationAmount: BigDecimal,
                              balance: BigDecimal)

case class Fundable(override val id: UUID,
                    name: String,
                    fundingTarget: BigDecimal,
                    balance: BigDecimal = 0) extends DonationReceiver(id)

case class FundableWithoutId(name: String,
                             fundingTarget: BigDecimal,
                             balance: BigDecimal)

case class DonationWithoutId(from: UUID, to: UUID, value: BigDecimal)

case class Donation(id: UUID, from: UUID, to: UUID, value: BigDecimal)
