package donatr

import java.util.UUID

import org.scalatest.{FlatSpec, Matchers}

class DonatrSpec extends FlatSpec with Matchers {
  final class EP extends EventPublisher {
    override def publish(event: Event): Unit = {}
  }

  trait Db {
    implicit val eventStore = new EventStore(s"jdbc:h2:mem:donatr-${UUID.randomUUID()};DB_CLOSE_DELAY=-1")
    implicit val ep = new EP()
    val donatr = new DonatrCore()
    val ledgerId: UUID = donatr.ledger.id
  }

  it should "create new Donater" in new Db {
    val donaterCreated = donatr.processCommand(CreateDonater(DonaterWithoutId("Foo", "Bar", 0)))
    donaterCreated should be(a[Right[_, DonaterCreated]])

    val donater = donatr.donaters(donaterCreated.right.get.donater.id)
    donater.balance should be (0)
  }

  it should "update the Donater name" in new Db {
    val donaterCreated = donatr.processCommand(CreateDonater(DonaterWithoutId("Foo", "Bar", 0)))
    donaterCreated should be(a[Right[_, DonaterCreated]])

    private val id = donaterCreated.right.get.donater.id
    donatr.processCommand(ChangeDonaterName(id, "fooNew"))
    donatr.donaters(id).name should be("fooNew")
  }

  it should "create new Donatable" in new Db {
    val donatableCreated = donatr.processCommand(CreateDonatable(DonatableWithoutId("Foo", "fooimg", 0, 0)))
    donatableCreated should be(a[Right[_, DonatableCreated]])

    val donatable = donatr.donatables(donatableCreated.right.get.donatable.id)
    donatable.balance should be (0)
  }

  it should "create new Fundable" in new Db {
    val fundableCreated = donatr.processCommand(CreateFundable(FundableWithoutId("Foo", "fooimg", 0, 0)))
    fundableCreated should be(a[Right[_, DonatableCreated]])

    val fundable = donatr.fundables(fundableCreated.right.get.fundable.id)
    fundable.balance should be (0)
  }

  it should "return NameTaken on duplicate Donaters name" in new Db {
    val donaterCreated1 = donatr.processCommand(CreateDonater(DonaterWithoutId("Foo", "Bar", 0)))
    val donaterCreated2 = donatr.processCommand(CreateDonater(DonaterWithoutId("Foo", "Bar", 0)))
    donaterCreated1 should be(a[Right[_, DonaterCreated]])
    donaterCreated2 should be(a[Left[NameTaken, _]])
  }

  it should "return NameTaken on duplicate Donatables name" in new Db {
    val donatableCreated1 = donatr.processCommand(CreateDonatable(DonatableWithoutId("Foo", "fooimg", 0, 0)))
    val donatableCreated2 = donatr.processCommand(CreateDonatable(DonatableWithoutId("Foo", "fooimg", 0, 0)))
    donatableCreated1 should be(a[Right[_, DonatableCreated]])
    donatableCreated2 should be(a[Left[NameTaken, _]])
  }

  it should "return NameTaken on duplicate Fundables name" in new Db {
    val fundableCreated1 = donatr.processCommand(CreateFundable(FundableWithoutId("Foo", "fooimg", 0, 0)))
    val fundableCreated2 = donatr.processCommand(CreateFundable(FundableWithoutId("Foo", "fooimg", 0, 0)))
    fundableCreated1 should be(a[Right[_, FundableCreated]])
    fundableCreated2 should be(a[Left[NameTaken, _]])
  }

  it should "throw BelowMinDonation with smaller value than minDonation" in new Db {
    val (fromId, toId) = mkDonaterAndDonatable(donatr)
    val donation = donatr.processCommand(CreateDonation(DonationWithoutId(fromId, toId, 0)))
    donation should be (a[Left[BelowMinDonationAmount, _]])
  }

  it should "create Donater, Donatable and Donation and have correct balances" in new Db {
    val (fromId, toId) = mkDonaterAndDonatable(donatr)
    val donation = mkDonation(donatr, fromId, toId)
    val donater = donatr.donaters(fromId)
    val donatable = donatr.donatables(toId)
    donater.balance should be (-3)
    donatable.balance should be (3)

    donater.balance should be (-3)
    donatable.balance should be (3)
  }

  it should "create Donater, Fundable and Donation and have correct balances" in new Db {
    val (fromId, toId) = mkDonaterAndFundable(donatr)
    val donation = mkDonation(donatr, fromId, toId)
    val donater = donatr.donaters(fromId)
    val fundable = donatr.fundables(toId)
    donater.balance should be (-3)
    fundable.balance should be (3)
  }

  it should "create Donater, Donation and have correct balances in Ledger and Donater" in new Db {
    val (fromId, toId) = (ledgerId, mkDonater(donatr))
    val donation = mkDonation(donatr, fromId, toId)
    val donater = donatr.donaters(toId)
    donater.balance should be (3)
    donatr.ledger.balance should be (-3)
  }

  it should "have correct state after rebuild" in new Db {
    val (fromId, toId) = mkDonaterAndDonatable(donatr)
    mkDonation(donatr, fromId, toId)

    donatr.donaters(fromId).balance should be (-3)
    donatr.donatables(toId).balance should be (3)

    donatr.resetState()

    donatr.donaters should be (Map.empty)

    donatr.rebuildState()

    donatr.donatables should not be Map.empty

    donatr.donaters(fromId).balance should be (-3)
    donatr.donatables(toId).balance should be (3)
  }

  it should "have same ledger after rebuild" in new Db {
    val initLedgerId = donatr.ledger.id

    donatr.resetState()

    donatr.donatables should be (Map.empty)

    donatr.rebuildState()

    donatr.donations should be (Map.empty)
  }

  private def mkDonaterAndDonatable(donatr: DonatrCore) = {
    (mkDonater(donatr), mkDonatable(donatr))
  }

  private def mkDonaterAndFundable(donatr: DonatrCore) = {
    (mkDonater(donatr), mkFundable(donatr))
  }

  private def mkDonatable(donatr: DonatrCore) = {
    val donatableCreated = donatr.processCommand(CreateDonatable(DonatableWithoutId("Foo", "fooimg", 1, 0)))
    donatableCreated should be(a[Right[_, DonatableCreated]])
    donatableCreated.right.get.donatable.id
  }

  private def mkDonater(donatr: DonatrCore) = {
    val donaterCreated = donatr.processCommand(CreateDonater(DonaterWithoutId("Foo", "Bar", 0)))
    donaterCreated should be(a[Right[_, DonaterCreated]])
    donaterCreated.right.get.donater.id
  }

  private def mkFundable(donatr: DonatrCore) = {
    val fundableCreated = donatr.processCommand(CreateFundable(FundableWithoutId("Foo", "fooimg", 0, 0)))
    fundableCreated should be(a[Right[_, FundableCreated]])
    fundableCreated.right.get.fundable.id
  }

  private def mkDonation(donatr: DonatrCore, fromId: UUID, toId: UUID, value: BigDecimal = 3) = {
    val donation = donatr.processCommand(CreateDonation(DonationWithoutId(fromId, toId, value)))
    donation should be(a[Right[_, DonationCreated]])
    donation
  }

  it should "throw UnknownEntity new Donation with random from/to" in new Db {
    val fromId = UUID.randomUUID()
    val toId = UUID.randomUUID()
    val event = donatr.processCommand(CreateDonation(DonationWithoutId(fromId, toId, 3)))
    event should be(a[Left[UnknownEntity, _]])
  }
}
