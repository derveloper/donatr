package donatr

import java.util.UUID

sealed trait BaseDonatable {
  val id: Option[UUID]
  val name: String
  val balance: BigDecimal
}

case class Donatable(
                      id: Option[UUID],
                      name: String,
                      balance: BigDecimal = 0
                    ) extends BaseDonatable

case class FixedValueDonatable(
                                id: Option[UUID],
                                name: String,
                                value: BigDecimal,
                                balance: BigDecimal = 0
                              ) extends BaseDonatable

case class Transaction(id: Option[UUID], from: Donatable, to: Donatable, value: BigDecimal)
