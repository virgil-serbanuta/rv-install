package com.runtimeverification.ktest.kthings

import com.runtimeverification.ktest.KThing

object ids {
  class Ids extends KThing
  case class Empty() extends Ids
  case class List(id: Id, ids: Ids) extends Ids
}
