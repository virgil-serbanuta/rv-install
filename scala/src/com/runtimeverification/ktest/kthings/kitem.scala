package com.runtimeverification.ktest.kthings

import com.runtimeverification.ktest.KThing
import com.runtimeverification.ktest.kthings.block.Block

object kitem {
  class KItem extends KThing
  case class DivLeftMissing(value : aexp.Aexp) extends KItem
  case class DivRightMissing(value : aexp.Aexp) extends KItem
  case class AddLeftMissing(value : aexp.Aexp) extends KItem
  case class AddRightMissing(value : aexp.Aexp) extends KItem
  case class LessOrEqualsLeftMissing(value : aexp.Aexp) extends KItem
  case class LessOrEqualsRightMissing(value : aexp.Aexp) extends KItem

  case class NotOperandMissing() extends KItem
  case class AssignmentRightMissing(id : Id) extends KItem
  case class IfConditionMissing(ithen : Block, ielse : Block) extends KItem
}
