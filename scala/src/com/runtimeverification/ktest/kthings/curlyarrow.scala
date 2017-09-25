package com.runtimeverification.ktest.kthings

import com.runtimeverification.ktest.KThing

object curlyarrow {
  class List extends KThing
  case class Empty() extends List
  case class Pair(kThing: KThing, reminder: List) extends List
}
