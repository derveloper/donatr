package donatr

import donatr.DonatrTypes._

trait CreateCommand[T] {
  def create(obj: T): Either[Throwable, Id]
}

object CreateCommand {
  def instance[T](f: T => Either[Throwable, Id]) = new CreateCommand[T] {
    override def create(obj: T): Either[Throwable, Id] = f(obj)
  }
}

trait ChangeNameCommand[T] {
  def changeName(obj: T, name: Name): Either[Throwable, Id]
}

object ChangeNameCommand {
  def instance[T](f: (T, String) => Either[Throwable, Id]) = new ChangeNameCommand[T] {
    override def changeName(obj: T, name: Name): Either[Throwable, Id] = f(obj, name)
  }
}

trait EventT[S, T] {
  def reduce(state: S, obj: T): S
}

object EventT {
  def instance[S, T](f: (S,T) => S) = new EventT[S, T] {
    override def reduce(state: S, obj: T): S = f(state, obj)
  }
}

object DonatrState {
  implicit val CreateDonaterE: EventT[DonatrState, DonaterCreated] = EventT.instance((state, d) =>
    state.copy(donaters = state.donaters + (d.donater.id -> d.donater)))

  implicit val CreateDonatableE: EventT[DonatrState, DonatableCreated] = EventT.instance((state, d) =>
    state.copy(donatables = state.donatables + (d.donatable.id -> d.donatable)))

  implicit val CreateFundableE: EventT[DonatrState, FundableCreated] = EventT.instance((state, d) =>
    state.copy(fundables = state.fundables + (d.fundable.id -> d.fundable)))

  implicit val DonaterNameChangedE: EventT[DonatrState, DonaterNameChanged] = EventT.instance((state, d) =>
    state.copy(donaters = state.donaters + (d.donaterId -> state.donaters(d.donaterId).copy(name = d.name))))
}

case class DonatrState(donaters: Map[Id, Donater] = Map.empty,
                       donatables: Map[Id, Donatable] = Map.empty,
                       fundables: Map[Id, Fundable] = Map.empty,
                       donations: Map[Id, Donation] = Map.empty,
                       ledger: Ledger)
{
  def reduce[S,T](state: S, entity: T)(implicit creator: EventT[S,T]): S = {
    creator.reduce(state, entity)
  }

  def apply(event: Event): DonatrState = event match {
    case LedgerCreated(Ledger(id, balance)) =>
      DonatrState(ledger = Ledger(id, balance))

    case DonaterCreated(Donater(id, name, email, balance)) =>
      copy(donaters = donaters + (id -> Donater(id, name, email, balance)))

    case DonaterNameChanged(id, name) =>
      copy(donaters = donaters + (id -> donaters(id).copy(name = name)))

    case DonatableCreated(Donatable(id, imageUrl, name, minDonationAmount, balance)) =>
      copy(donatables = donatables + (id -> Donatable(id, imageUrl, name, minDonationAmount, balance)))

    case FundableCreated(Fundable(id, name, imageUrl, fundingTarget, balance)) =>
      copy(fundables = fundables + (id -> Fundable(id, name, imageUrl, fundingTarget, balance)))

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
      if (donaters.contains(entityId)) {
        val d = donaters(entityId)
        copy(donaters = donaters + (entityId -> d.copy(balance = d.balance - depositValue)))
      } else {
        copy(ledger = ledger.copy(balance = ledger.balance - depositValue))
      }

    case _ => throw UnknownEvent(event)
  }
}
