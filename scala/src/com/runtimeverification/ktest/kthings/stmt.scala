package com.runtimeverification.ktest.kthings

import com.runtimeverification.ktest.KThing
import com.runtimeverification.ktest.kthings.aexp.Aexp

object stmt {
  class Stmt extends KThing
  case class SBlock(sblock: block.Block) extends Stmt
  case class Assign(id: Id, aexp: Aexp) extends Stmt
  case class If(condition: bexp.Bexp, ithen: block.Block, ielse: block.Block) extends Stmt
  case class While(condition: bexp.Bexp, body: block.Block) extends Stmt
  case class Sequence(first: stmt.Stmt, second: stmt.Stmt) extends Stmt
}
