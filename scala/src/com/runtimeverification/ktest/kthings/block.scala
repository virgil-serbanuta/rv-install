package com.runtimeverification.ktest.kthings

import com.runtimeverification.ktest.KThing

object block {
  class Block extends KThing
  case class Empty() extends block.Block
  case class BStmt(statement: stmt.Stmt) extends block.Block
}
