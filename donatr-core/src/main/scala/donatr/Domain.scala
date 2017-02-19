package donatr


import donatr.DonatrTypes._

case class Ledger(id: Id,
                  balance: Balance = 0)

case class Donater(id: Id,
                   name: Name,
                   email: Email,
                   balance: BigDecimal)

case class DonaterWithoutId(name: Name,
                            email: Email,
                            balance: BigDecimal)

case class Donatable(id: Id,
                     name: Name,
                     imageUrl: ImageUrl,
                     minDonationAmount: MinDonation,
                     balance: Balance)

case class DonatableWithoutId(name: Name,
                              imageUrl: ImageUrl,
                              minDonationAmount: MinDonation,
                              balance: Balance)

case class Fundable(id: Id,
                    name: Name,
                    imageUrl: ImageUrl,
                    fundingTarget: FundingTarget,
                    balance: Balance = 0)

case class FundableWithoutId(name: Name,
                             imageUrl: ImageUrl,
                             fundingTarget: FundingTarget,
                             balance: Balance)

case class Donation(id: Id,
                    from: Id,
                    to: Id,
                    value: Value)

case class DonationWithoutId(from: Id,
                             to: Id,
                             value: Value)

case class DonationWithoutIdAndFrom(to: Id,
                                    value: Value)

