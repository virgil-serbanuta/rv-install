package com.runtimeverification.ktest.kthings

import com.runtimeverification.ktest.KThing

object bexp {
  class Bexp extends KThing
  case class Bool(value : KBool) extends Bexp
  case class LessOrEquals(left : aexp.Aexp, right : aexp.Aexp) extends Bexp
  case class Not(operand : Bexp) extends Bexp
  case class And(left : Bexp, right : Bexp) extends Bexp
}
