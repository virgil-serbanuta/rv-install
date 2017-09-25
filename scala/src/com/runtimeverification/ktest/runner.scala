package com.runtimeverification.ktest

import com.runtimeverification.ktest.kthings._

object runner {
  def run(firstConfiguration: Configuration) : Configuration = {
    var configuration = firstConfiguration
    while (true) {
      step(configuration) match {
        case None => return configuration
        case Some(nextConfiguration) => configuration = nextConfiguration
      }
    }
    // I'm not sure why this is needed, but Idea had a compile error here.
    configuration
  }

  def step(configuration: Configuration) : Option[Configuration] = {
    val (firstElement: KThing, firstReminder: curlyarrow.List) =
      configuration.k match {
        case curlyarrow.Empty() => return None
        case curlyarrow.Pair(first, reminder) => (first, reminder)
      }
    val (maybeSecondElement: Option[KThing], secondReminder: curlyarrow.List) =
      firstReminder match {
        case curlyarrow.Empty() => (None, curlyarrow.Empty())
        case curlyarrow.Pair(second, reminder) => (Some(second), reminder)
      }
    val firstLookup =
      firstElement match {
        case aexp.AId(id) => configuration.state.get(id)
        case _ => None
      }

    (firstElement, firstLookup, maybeSecondElement) match {
        // Structural rules.
      case (block.Empty(), _, _) => Some(Configuration(firstReminder, configuration.state))
      case (block.BStmt(stmt), _, _) => Some(Configuration(curlyarrow.Pair(stmt, firstReminder), configuration.state))
      case (stmt.SBlock(sblock), _, _) => Some(Configuration(curlyarrow.Pair(sblock, firstReminder), configuration.state))
      case (stmt.Sequence(first, second), _, _) =>
        Some(Configuration(curlyarrow.Pair(first, curlyarrow.Pair(second, firstReminder)), configuration.state))
      case (w@stmt.While(condition, body), _, _) =>
        Some(Configuration(curlyarrow.Pair(
          stmt.If(
            condition,
            block.BStmt(stmt.Sequence(stmt.SBlock(body), w)),
            block.Empty()), firstReminder),
          configuration.state))
      case (Pgm(ids.Empty(), statement), _, _) =>
        Some(Configuration(curlyarrow.Pair(statement, firstReminder), configuration.state))

        // Non-structural rules.
      case (aexp.AId(_), Some(value), _) =>
        Some(Configuration(curlyarrow.Pair(value, firstReminder), configuration.state))
      case (aexp.Div(aexp.AInt(KInt(left)), aexp.AInt(KInt(right))), _, _) if right != 0 =>
        Some(Configuration(curlyarrow.Pair(aexp.AInt(KInt(left/right)), firstReminder), configuration.state))
      case (aexp.Add(aexp.AInt(KInt(left)), aexp.AInt(KInt(right))), _, _) =>
        Some(Configuration(curlyarrow.Pair(aexp.AInt(KInt(left + right)), firstReminder), configuration.state))
      case (bexp.LessOrEquals(aexp.AInt(KInt(left)), aexp.AInt(KInt(right))), _, _) =>
        Some(Configuration(curlyarrow.Pair(bexp.Bool(KBool(left <= right)), firstReminder), configuration.state))
      case (bexp.Not(bexp.Bool(KBool(value))), _, _) =>
        Some(Configuration(curlyarrow.Pair(bexp.Bool(KBool(!value)), firstReminder), configuration.state))
      case (bexp.And(bexp.Bool(KBool(true)), second), _, _) =>
        Some(Configuration(curlyarrow.Pair(second, firstReminder), configuration.state))
      case (bexp.And(bexp.Bool(KBool(false)), _), _, _) =>
        Some(Configuration(curlyarrow.Pair(bexp.Bool(KBool(false)), firstReminder), configuration.state))
      case (stmt.Assign(id@Id(_), aexp@aexp.AInt(_)), _, _) =>
        Some(Configuration(firstReminder, configuration.state + (id -> aexp)))
      case (stmt.If(bexp.Bool(KBool(true)), ithen, _), _, _) =>
        Some(Configuration(curlyarrow.Pair(ithen, firstReminder), configuration.state))
      case (stmt.If(bexp.Bool(KBool(false)), _, ielse), _, _) =>
        Some(Configuration(curlyarrow.Pair(ielse, firstReminder), configuration.state))
      case (Pgm(ids.List(id@Id(_), idList), statement), _, _) =>
        Some(Configuration(
          curlyarrow.Pair(Pgm(idList, statement), firstReminder),
          configuration.state + (id -> aexp.AInt(KInt(0)))))

        // Expression decomposition and rebuilding.
      case (aexp.Div(left, right), _, _) if !left.isInstanceOf[aexp.AInt] =>
        Some(Configuration(
          curlyarrow.Pair(left, curlyarrow.Pair(kitem.DivLeftMissing(right), firstReminder)),
          configuration.state))
      case (left@aexp.AInt(_), _, Some(kitem.DivLeftMissing(right))) =>
        Some(Configuration(curlyarrow.Pair(aexp.Div(left, right), secondReminder), configuration.state))
      case (aexp.Div(left, right), _, _) if left.isInstanceOf[aexp.AInt] && !right.isInstanceOf[aexp.AInt] =>
        Some(Configuration(
          curlyarrow.Pair(right, curlyarrow.Pair(kitem.DivRightMissing(left), firstReminder)),
          configuration.state))
      case (right@aexp.AInt(_), _, Some(kitem.DivRightMissing(left))) =>
        Some(Configuration(curlyarrow.Pair(aexp.Div(left, right), secondReminder), configuration.state))

      case (aexp.Add(left, right), _, _) if !left.isInstanceOf[aexp.AInt] =>
        Some(Configuration(
          curlyarrow.Pair(left, curlyarrow.Pair(kitem.AddLeftMissing(right), firstReminder)),
          configuration.state))
      case (left@aexp.AInt(_), _, Some(kitem.AddLeftMissing(right))) =>
        Some(Configuration(curlyarrow.Pair(aexp.Add(left, right), secondReminder), configuration.state))
      case (aexp.Add(left, right), _, _) if left.isInstanceOf[aexp.AInt] && !right.isInstanceOf[aexp.AInt] =>
        Some(Configuration(
          curlyarrow.Pair(right, curlyarrow.Pair(kitem.AddRightMissing(left), firstReminder)),
          configuration.state))
      case (right@aexp.AInt(_), _, Some(kitem.AddRightMissing(left))) =>
        Some(Configuration(curlyarrow.Pair(aexp.Add(left, right), secondReminder), configuration.state))

      case (bexp.LessOrEquals(left, right), _, _) if !left.isInstanceOf[aexp.AInt] =>
        Some(Configuration(
          curlyarrow.Pair(left, curlyarrow.Pair(kitem.LessOrEqualsLeftMissing(right), firstReminder)),
          configuration.state))
      case (left@aexp.AInt(_), _, Some(kitem.LessOrEqualsLeftMissing(right))) =>
        Some(Configuration(curlyarrow.Pair(bexp.LessOrEquals(left, right), secondReminder), configuration.state))
      case (bexp.LessOrEquals(left, right), _, _) if left.isInstanceOf[aexp.AInt] && !right.isInstanceOf[aexp.AInt] =>
        Some(Configuration(
          curlyarrow.Pair(right, curlyarrow.Pair(kitem.LessOrEqualsRightMissing(left), firstReminder)),
          configuration.state))
      case (right@aexp.AInt(_), _, Some(kitem.LessOrEqualsRightMissing(left))) =>
        Some(Configuration(curlyarrow.Pair(bexp.LessOrEquals(left, right), secondReminder), configuration.state))

      case (bexp.Not(condition), _, _) if !condition.isInstanceOf[bexp.Bool] =>
        Some(Configuration(
          curlyarrow.Pair(condition, curlyarrow.Pair(kitem.NotOperandMissing(), firstReminder)),
          configuration.state))
      case (right@bexp.Bool(_), _, Some(kitem.NotOperandMissing())) =>
        Some(Configuration(curlyarrow.Pair(bexp.Not(right), secondReminder), configuration.state))

      case (stmt.Assign(id, expression), _, _) if !expression.isInstanceOf[aexp.AInt] =>
        Some(Configuration(
          curlyarrow.Pair(expression, curlyarrow.Pair(kitem.AssignmentRightMissing(id), firstReminder)),
          configuration.state))
      case (expression@aexp.AInt(_), _, Some(kitem.AssignmentRightMissing(id))) =>
        Some(Configuration(curlyarrow.Pair(stmt.Assign(id, expression), secondReminder), configuration.state))

      case (stmt.If(condition, ithen, ielse), _, _) if !condition.isInstanceOf[bexp.Bool] =>
        Some(Configuration(
          curlyarrow.Pair(condition, curlyarrow.Pair(kitem.IfConditionMissing(ithen, ielse), firstReminder)),
          configuration.state))
      case (expression@bexp.Bool(_), _, Some(kitem.IfConditionMissing(ithen, ielse))) =>
        Some(Configuration(curlyarrow.Pair(stmt.If(expression, ithen, ielse), secondReminder), configuration.state))
      case _ => None
    }
  }
}
