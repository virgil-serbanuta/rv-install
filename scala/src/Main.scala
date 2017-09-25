import com.runtimeverification.ktest.{Configuration, KThing, runner}
import com.runtimeverification.ktest.kthings._

object Main {
  def main(args: Array[String]): Unit = {
    val pgm = loadProgram()
    1 to 10000 foreach {
      _ => runner.run(Configuration(curlyarrow.Pair(pgm, curlyarrow.Empty()), Map()))
    }
    println(runner.run(Configuration(curlyarrow.Pair(pgm, curlyarrow.Empty()), Map())))
    println("Hello world!")
  }

  private def loadProgram() : KThing = {
    /*
      int n, sum,
      n = 100,
      sum = 0,
      while (!(n <= 0)) {
        sum = sum + n,
        n = n + -1,
      }
    */
    Pgm(
      ids.List(Id("n"), ids.List(Id("sum"), ids.Empty())),
      stmt.Sequence(
        stmt.Assign(Id("n"), aexp.AInt(KInt(100))),
        stmt.Sequence(
          stmt.Assign(Id("sum"), aexp.AInt(KInt(0))),
          stmt.While(
            bexp.Not(bexp.LessOrEquals(aexp.AId(Id("n")), aexp.AInt(KInt(0)))),
            block.BStmt(stmt.Sequence(
              stmt.Assign(Id("sum"), aexp.Add(aexp.AId(Id("sum")), aexp.AId(Id("n")))),
              stmt.Assign(Id("n"), aexp.Add(aexp.AId(Id("n")), aexp.AInt(KInt(-1))))))))))
  }
}
