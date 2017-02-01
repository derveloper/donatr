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

    case DonationCreated(Donation(id, from, to, value)) =>
      copy(donations = donations + (id -> Donation(id, from, to, value)))

    case Deposited(_, entityId, depositValue) =>
      (donaters.contains(entityId), donatables.contains(entityId), fundables.contains(entityId)) match {
        case (true, false, false) =>
          val d = donaters(entityId)
          copy(donaters = donaters + (entityId -> d.copy(balance = d.balance + depositValue)))
        case (false, true, false) =>
          val d = donatables(entityId)
          copy(donatables = donatables + (entityId -> d.copy(balance = d.balance + depositValue)))
        case (false, false, true) =>
          val d = fundables(entityId)
          copy(fundables = fundables + (entityId -> d.copy(balance = d.balance + depositValue)))
        case _ => throw UnknownEntity(entityId)
      }

    case Withdrawn(_, entityId, depositValue) =>
      (donaters.contains(entityId), donatables.contains(entityId), fundables.contains(entityId)) match {
        case (true, false, false) =>
          val d = donaters(entityId)
          copy(donaters = donaters + (entityId -> d.copy(balance = d.balance - depositValue)))
        case (false, true, false) =>
          val d = donatables(entityId)
          copy(donatables = donatables + (entityId -> d.copy(balance = d.balance - depositValue)))
        case (false, false, true) =>
          val d = fundables(entityId)
          copy(fundables = fundables + (entityId -> d.copy(balance = d.balance - depositValue)))
        case _ => throw UnknownEntity(entityId)
      }

    case _ => throw UnknownEvent(event)
  }
}
