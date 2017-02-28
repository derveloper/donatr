package donatr

import java.util.UUID

import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, Matchers}

trait TestT {
  def foo(bar: String): Unit
}

class DonatrCoreMockSpec extends FlatSpec with Matchers with MockFactory {
  it should "call persist and publish on create Donatr" in {
    val donatr = mock[TestT]
    val donater = Donater(UUID.randomUUID(), "Foo", "bar", 0)
    (donatr.foo _).expects("foo")
    donatr.foo("bar")
  }
}
