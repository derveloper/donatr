package donatr

import java.util.UUID

import org.scalatest.{FlatSpec, Matchers}

class DonatrSpec extends FlatSpec with Matchers {
  trait Db {
    val eventStore = new EventStore(s"jdbc:h2:mem:donatr-${UUID.randomUUID()};DB_CLOSE_DELAY=-1")
    val donatr = new DonatrCore(eventStore)
  }

  it should "create new Donater" in new Db {
    val donaterCreated = donatr.processCommand(CreateDonater(DonaterWithoutId("Foo", "Bar", 0)))
    donaterCreated should be(a[Right[_, DonaterCreated]])

    val donater = donatr.state.donaters(donaterCreated.right.get.donater.id)
    donater.balance should be (0)
  }

  it should "create new Donatable" in new Db {
    val donatableCreated = donatr.processCommand(CreateDonater(DonaterWithoutId("Foo", "Bar", 0)))
    donatableCreated should be(a[Right[_, DonatableCreated]])

    val donatable = donatr.state.donaters(donatableCreated.right.get.donater.id)
    donatable.balance should be (0)
  }

  it should "return NameTaken on duplicate Donaters name" in new Db {
    val donaterCreated1 = donatr.processCommand(CreateDonater(DonaterWithoutId("Foo", "Bar", 0)))
    val donaterCreated2 = donatr.processCommand(CreateDonater(DonaterWithoutId("Foo", "Bar", 0)))
    donaterCreated1 should be(a[Right[_, DonaterCreated]])
    donaterCreated2 should be(a[Left[_, DonaterCreated]])
  }

  it should "create new Donation" in new Db {
    val donaterCreated = donatr.processCommand(CreateDonater(DonaterWithoutId("Foo", "Bar", 0)))
    val donatableCreated = donatr.processCommand(CreateDonatable(DonatableWithoutId("Foo", 1, 0)))
    val fromId = donaterCreated.right.get.donater.id
    val toId = donatableCreated.right.get.donatable.id
    donatr.processCommand(CreateDonation(DonationWithoutId(fromId, toId, 3)))
    val donater = donatr.state.donaters(fromId)
    val donatable = donatr.state.donatables(toId)
    donater.balance should be (-3)
    donatable.balance should be (3)
  }
}
