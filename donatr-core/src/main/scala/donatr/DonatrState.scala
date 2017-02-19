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

  implicit val CreateDonationE: EventT[DonatrState, DonationCreated] = EventT.instance((state, d) =>
    state.copy(donations = state.donations + (d.donation.id -> Donation(
      d.donation.id, d.donation.from, d.donation.to, d.donation.value))))

  implicit val WithdrawnE: EventT[DonatrState, Withdrawn] = EventT.instance((state, d) => {
    if (state.donaters.contains(d.entityId)) {
      val donater = state.donaters(d.entityId)
      state.copy(donaters = state.donaters + (d.entityId -> donater.copy(balance = donater.balance - d.withdrawValue)))
    } else {
      state.copy(ledger = state.ledger.copy(balance = state.ledger.balance - d.withdrawValue))
    }
  })

  implicit val DepositedE: EventT[DonatrState, Deposited] = EventT.instance((state, d) => {
    (state.donaters.contains(d.entityId),
      state.donatables.contains(d.entityId),
      state.fundables.contains(d.entityId)) match {
      case (true, false, false) =>
        val donater = state.donaters(d.entityId)
        state.copy(
          donaters = state.donaters + (d.entityId -> donater.copy(balance = donater.balance + d.depositValue)))
      case (false, true, false) =>
        val donatable = state.donatables(d.entityId)
        state.copy(
          donatables = state.donatables + (d.entityId -> donatable.copy(balance = donatable.balance + d.depositValue)))
      case (false, false, true) =>
        val fundable = state.fundables(d.entityId)
        state.copy(
          fundables = state.fundables + (d.entityId -> fundable.copy(balance = fundable.balance + d.depositValue)))
      case _ => throw UnknownEntity(d.entityId)
    }
  })
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
}
