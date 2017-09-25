package com.runtimeverification.ktest.kthings

import com.runtimeverification.ktest.KThing

object aexp {
  class Aexp extends KThing
  case class AInt(int : KInt) extends Aexp
  case class AId(id : Id) extends Aexp
  case class Add(left: Aexp, right: Aexp) extends Aexp
  case class Div(left: Aexp, right: Aexp) extends Aexp
}
