package donatr

import java.util.UUID


case class DonatrState(
                        donaters: Map[UUID, Donater] = Map.empty,
                        donatables: Map[UUID, Donatable] = Map.empty,
                        fundables: Map[UUID, Fundable] = Map.empty,
                        donations: Map[UUID, Donation] = Map.empty
                      ) {

  def apply(event: Event): DonatrState = event match {
    case DonaterCreated(Donater(id, name, email, balance)) =>
      copy(donaters = donaters + (id -> Donater(id, name, email, balance)))
    case DonatableCreated(Donatable(id, name, minDonationAmount, balance)) =>
      copy(donatables = donatables + (id -> Donatable(id, name, minDonationAmount, balance)))
    case FundableCreated(Fundable(id, name, fundingTarget, balance)) =>
      copy(fundables = fundables + (id -> Fundable(id, name, fundingTarget, balance)))
    case _ => throw UnknownEvent(event)
  }
}
