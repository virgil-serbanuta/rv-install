package com.runtimeverification.ktest;

import com.runtimeverification.ktest.configuration.Attribute;
import com.runtimeverification.ktest.configuration.Cell;
import com.runtimeverification.ktest.nonterminals.Aexp;
import com.runtimeverification.ktest.nonterminals.Bexp;
import com.runtimeverification.ktest.nonterminals.Block;
import com.runtimeverification.ktest.nonterminals.Bool;
import com.runtimeverification.ktest.nonterminals.Id;
import com.runtimeverification.ktest.nonterminals.Ids;
import com.runtimeverification.ktest.nonterminals.Int;
import com.runtimeverification.ktest.nonterminals.KItem;
import com.runtimeverification.ktest.nonterminals.KResult;
import com.runtimeverification.ktest.nonterminals.Pgm;
import com.runtimeverification.ktest.nonterminals.Stmt;
import com.runtimeverification.ktest.rule.operations.Operation;
import com.runtimeverification.ktest.rule.ruleterms.Anything;
import com.runtimeverification.ktest.rule.ruleterms.MappingTerm;
import com.runtimeverification.ktest.rule.RuleCell;
import com.runtimeverification.ktest.rule.RuleFlags;
import com.runtimeverification.ktest.rule.RuleTerm;
import com.runtimeverification.ktest.rule.ruleterms.Sequence;
import com.runtimeverification.ktest.rule.ruleterms.Terminal;
import com.runtimeverification.ktest.rule.ruleterms.Transform;
import com.runtimeverification.ktest.rule.ruleterms.Variable;
import com.runtimeverification.ktest.rule.operations.BoolAnd;
import com.runtimeverification.ktest.rule.operations.BoolNot;
import com.runtimeverification.ktest.rule.operations.IntAddition;
import com.runtimeverification.ktest.rule.operations.IntDivision;
import com.runtimeverification.ktest.rule.operations.IntLessOrEquals;
import com.runtimeverification.ktest.rule.operations.IntNotEquals;
import com.runtimeverification.ktest.tools.KMap;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        KThing pgm = loadProgram(args);
        Configuration initialConfiguration = Configuration.of(
                Cell.of("k", pgm, Attribute.of("color", "green")),
                Cell.of("state", new KMap(), Attribute.of("color", "red")),
                Cell.of("exit-code", Int.of("0"), Attribute.of("exit", "true")));
        List<Rule> rules = impRules();
        System.out.println(Runner.run(initialConfiguration, rules));
    }

    private static Optional<Configuration> step(Configuration configuration) {
        if (configuration.getK().getTermCount() == 0) {
            return Optional.empty();
        }
        final KThing firstKCellItem = configuration.getK().getTerm(0);

        assert configuration.getState().getTermCount() == 1;
        final KThing stateCellItem = configuration.getState().getTerm(0);
        assert stateCellItem instanceof KMap;
        final KMap stateCellMap = (KMap)stateCellItem;

        {
            KThing value = stateCellMap.getMap().get(firstKCellItem);
            if (value != null) {
                return Optional.of(configuration.replaceKCell(
                        configuration.getK().replacePrefix(1, value)));
            }
        }
        {
            if (firstKCellItem instanceof Aexp.DivAexp) {
                Aexp.DivAexp div = (Aexp.DivAexp) firstKCellItem;
                if ((div.getFirst() instanceof Aexp.IntAexp) && (div.getSecond() instanceof Aexp.IntAexp)) {
                    Aexp.IntAexp second = (Aexp.IntAexp) div.getSecond();
                    int secondValue = second.getInt().getValue();
                    if (secondValue != 0) {
                        Aexp.IntAexp first = (Aexp.IntAexp) div.getFirst();
                        return Optional.of(configuration.replaceKCell(
                                configuration.getK().replacePrefix(1, Aexp.aint(Int.of(
                                        first.getInt().getValue() / secondValue)))));
                    }
                }
            }
        }
        {
            if (firstKCellItem instanceof Aexp.AddAexp) {
                Aexp.AddAexp add = (Aexp.AddAexp) firstKCellItem;
                if ((add.getFirst() instanceof Aexp.IntAexp) && (add.getSecond() instanceof Aexp.IntAexp)) {
                    Aexp.IntAexp first = (Aexp.IntAexp) add.getFirst();
                    Aexp.IntAexp second = (Aexp.IntAexp) add.getSecond();
                    return Optional.of(configuration.replaceKCell(
                            configuration.getK().replacePrefix(1, Aexp.aint(Int.of(
                                    first.getInt().getValue() + second.getInt().getValue())))));
                }
            }
        }
        {
            if (firstKCellItem instanceof Bexp.LessBexp) {
                Bexp.LessBexp lessOrEquals = (Bexp.LessBexp) firstKCellItem;
                if ((lessOrEquals.getFirst() instanceof Aexp.IntAexp) && (lessOrEquals.getSecond() instanceof Aexp.IntAexp)) {
                    Aexp.IntAexp first = (Aexp.IntAexp) lessOrEquals.getFirst();
                    Aexp.IntAexp second = (Aexp.IntAexp) lessOrEquals.getSecond();
                    return Optional.of(configuration.replaceKCell(
                            configuration.getK().replacePrefix(1, Bexp.bool(Bool.of(
                                    first.getInt().getValue() <= second.getInt().getValue())))));
                }
            }
        }
        {
            if (firstKCellItem instanceof Bexp.NotBexp) {
                Bexp.NotBexp not = (Bexp.NotBexp) firstKCellItem;
                if ((not.getOperand() instanceof Bexp.BoolBexp)) {
                    Bexp.BoolBexp operand = (Bexp.BoolBexp) not.getOperand();
                    return Optional.of(configuration.replaceKCell(
                            configuration.getK().replacePrefix(1, Bexp.bool(Bool.of(
                                    !operand.getBool().getValue())))));
                }
            }
        }
        return Arrays.asList(
                Rule.of(
                        RuleCell.of(
                                "k",
                                Transform.of(
                                        Sequence.of(Terminal.of("true"), Terminal.of("&&"), Variable.of("B")),
                                        Resolver.of("B", KThing.class)),
                                Anything.of()),
                        RuleCell.of("state", Anything.of())
                ),
                Rule.of(
                        RuleCell.of(
                                "k",
                                Transform.of(
                                        Sequence.of(Terminal.of("false"), Terminal.of("&&"), Variable.of("B")),
                                        Bool.of("false")),
                                Anything.of()),
                        RuleCell.of("state", Anything.of())
                ),
                Rule.of(
                        RuleFlags.of(RuleFlags.Type.STRUCTURAL),
                        RuleCell.of(
                                "k",
                                Transform.of(
                                        Sequence.of(Terminal.of("{"), Terminal.of("}")),
                                        KThingSource.empty()),
                                Anything.of()),
                        RuleCell.of("state", Anything.of())
                ),
                Rule.of(
                        RuleFlags.of(RuleFlags.Type.STRUCTURAL),
                        RuleCell.of(
                                "k",
                                Transform.of(
                                        Sequence.of(Terminal.of("{"), Variable.of("S"), Terminal.of("}")),
                                        Resolver.of("S", KThing.class)),
                                Anything.of()),
                        RuleCell.of("state", Anything.of())
                ),
                Rule.of(
                        RuleCell.of(
                                "k",
                                Transform.of(
                                        Sequence.of(Variable.of("X"), Terminal.of("="), Variable.of("I", Int.class)),
                                        KThingSource.empty()),
                                Anything.of()),
                        RuleCell.of("state",
                                Anything.of(),
                                MappingTerm.of(
                                        Resolver.of("X", KThing.class),
                                        Transform.of(Variable.ignored(), Resolver.of("I", KThing.class))))
                ),
                Rule.of(
                        RuleCell.of(
                                "k",
                                Transform.of(
                                        Sequence.of(Variable.of("S1", Stmt.class), Variable.of("S2", Stmt.class)),
                                        Resolver.of("S1", KThing.class),
                                        Resolver.of("S2", KThing.class)),
                                Anything.of()),
                        RuleCell.of("state", Variable.ignored())
                ),
                Rule.of(
                        RuleCell.of(
                                "k",
                                Transform.of(
                                        Sequence.of(
                                                Terminal.of("if"), Terminal.of("("), Terminal.of("true"),
                                                Terminal.of(")"), Variable.of("S"), Terminal.of("else"),
                                                Variable.ignored()),
                                        Resolver.of("S", KThing.class)),
                                Anything.of()),
                        RuleCell.of("state", Variable.ignored())
                ),
                Rule.of(
                        RuleCell.of(
                                "k",
                                Transform.of(
                                        Sequence.of(
                                                Terminal.of("if"), Terminal.of("("), Terminal.of("false"), Terminal.of(")"),
                                                Variable.ignored(), Terminal.of("else"), Variable.of("S")),
                                        Resolver.of("S", KThing.class)),
                                Anything.of()),
                        RuleCell.of("state", Variable.ignored())
                ),
                Rule.of(
                        RuleFlags.of(RuleFlags.Type.STRUCTURAL),
                        RuleCell.of(
                                "k",
                                Transform.of(
                                        Sequence.of(
                                                Terminal.of("while"), Terminal.of("("), Variable.of("B"),
                                                Terminal.of(")"), Variable.of("S")),
                                        Stmt.sif(
                                                Resolver.of("B", Bexp.class),
                                                Block.stmt(Stmt.sequence(
                                                        Stmt.block(Resolver.of("S", Block.class)),
                                                        Stmt.swhile(
                                                                Resolver.of("B", Bexp.class),
                                                                Resolver.of("S", Block.class)))),
                                                Block.empty())),
                                Anything.of())
                ),
                Rule.of(
                        RuleCell.of(
                                "k",
                                Sequence.of(
                                        Terminal.of("int"),
                                        Transform.of(
                                                Sequence.of(
                                                        Variable.of("X"),
                                                        Terminal.of(","),
                                                        Variable.of("Xs")),
                                                Resolver.of("Xs", KThing.class))),
                                Anything.of()),
                        RuleCell.of(
                                "state",
                                MappingTerm.of(
                                        Resolver.of("X", KThing.class),
                                        Transform.of(Variable.ignored(), Resolver.of("I", KThing.class))))
                ),
                Rule.of(
                        RuleFlags.of(RuleFlags.Type.STRUCTURAL),
                        RuleCell.of(
                                "k",
                                Transform.of(
                                        Sequence.of(
                                                Terminal.of("int"),
                                                Builtins.empty(Ids.class),
                                                Terminal.of(";"),
                                                Variable.of("S")),
                                        Resolver.of("S", KThing.class)))
                ),

                Rule.of(
                        RuleCondition.of(BoolNot.of(Builtins.is(KResult.class, Resolver.of("E1", KThing.class)))),
                        RuleCell.of(
                                "k",
                                Transform.of(
                                        Sequence.of(Variable.of("E1"), Terminal.of("/"), Variable.of("E2")),
                                        Resolver.of("E1", KThing.class),
                                        KItem.divisionLeftMissing(Resolver.of("E2", Aexp.class))),
                                Anything.of())
                ),
                Rule.of(
                        RuleCondition.of(Builtins.is(KResult.class, Resolver.of("R", KThing.class))),
                        RuleCell.of(
                                "k",
                                Transform.of(
                                        Arrays.asList(
                                                Variable.of("R"),
                                                Sequence.of(Terminal.of("[]"), Terminal.of("/"), Variable.of("E2"))),
                                        Aexp.div(Resolver.of("R", Aexp.class), Resolver.of("E2", Aexp.class))),
                                Anything.of())
                ),
                Rule.of(
                        RuleCondition.of(
                                BoolAnd.of(
                                        Builtins.is(KResult.class, Resolver.of("E1", KThing.class)),
                                        BoolNot.of(Builtins.is(KResult.class, Resolver.of("E2", KThing.class))))),
                        RuleCell.of(
                                "k",
                                Transform.of(
                                        Sequence.of(Variable.of("E1"), Terminal.of("/"), Variable.of("E2")),
                                        Resolver.of("E2", KThing.class),
                                        KItem.divisionRightMissing(Resolver.of("E2", Aexp.class))),
                                Anything.of())
                ),
                Rule.of(
                        RuleCondition.of(Builtins.is(KResult.class, Resolver.of("R", KThing.class))),
                        RuleCell.of(
                                "k",
                                Transform.of(
                                        Arrays.asList(
                                                Variable.of("R"),
                                                Sequence.of(Terminal.of("E1"), Terminal.of("/"), Variable.of("[]"))),
                                        Aexp.div(Resolver.of("E1", Aexp.class), Resolver.of("R", Aexp.class))),
                                Anything.of())
                ),

                Rule.of(
                        RuleCondition.of(BoolNot.of(Builtins.is(KResult.class, Resolver.of("E1", KThing.class)))),
                        RuleCell.of(
                                "k",
                                Transform.of(
                                        Sequence.of(Variable.of("E1"), Terminal.of("+"), Variable.of("E2")),
                                        Resolver.of("E1", KThing.class),
                                        KItem.additionLeftMissing(Resolver.of("E2", Aexp.class))),
                                Anything.of())
                ),
                Rule.of(
                        RuleCondition.of(Builtins.is(KResult.class, Resolver.of("R", KThing.class))),
                        RuleCell.of(
                                "k",
                                Transform.of(
                                        Arrays.asList(
                                                Variable.of("R"),
                                                Sequence.of(Terminal.of("[]"), Terminal.of("+"), Variable.of("E2"))),
                                        Aexp.add(Resolver.of("R", Aexp.class), Resolver.of("E2", Aexp.class))),
                                Anything.of())
                ),
                Rule.of(
                        RuleCondition.of(
                                BoolAnd.of(
                                        Builtins.is(KResult.class, Resolver.of("E1", KThing.class)),
                                        BoolNot.of(Builtins.is(KResult.class, Resolver.of("E2", KThing.class))))),
                        RuleCell.of(
                                "k",
                                Transform.of(
                                        Sequence.of(Variable.of("E1"), Terminal.of("+"), Variable.of("E2")),
                                        Resolver.of("E2", KThing.class),
                                        KItem.additionRightMissing(Resolver.of("E2", Aexp.class))),
                                Anything.of())
                ),
                Rule.of(
                        RuleCondition.of(Builtins.is(KResult.class, Resolver.of("R", KThing.class))),
                        RuleCell.of(
                                "k",
                                Transform.of(
                                        Arrays.asList(
                                                Variable.of("R"),
                                                Sequence.of(Terminal.of("E1"), Terminal.of("+"), Variable.of("[]"))),
                                        Aexp.add(Resolver.of("E1", Aexp.class), Resolver.of("R", Aexp.class))),
                                Anything.of())
                ),

                Rule.of(
                        RuleCondition.of(BoolNot.of(Builtins.is(KResult.class, Resolver.of("E1", KThing.class)))),
                        RuleCell.of(
                                "k",
                                Transform.of(
                                        Sequence.of(Variable.of("E1"), Terminal.of("<="), Variable.of("E2")),
                                        Resolver.of("E1", KThing.class),
                                        KItem.lessOrEqualsLeftMissing(Resolver.of("E2", Aexp.class))),
                                Anything.of())
                ),
                Rule.of(
                        RuleCondition.of(Builtins.is(KResult.class, Resolver.of("R", KThing.class))),
                        RuleCell.of(
                                "k",
                                Transform.of(
                                        Arrays.asList(
                                                Variable.of("R"),
                                                Sequence.of(Terminal.of("[]"), Terminal.of("<="), Variable.of("E2"))),
                                        Bexp.less(Resolver.of("R", Aexp.class), Resolver.of("E2", Aexp.class))),
                                Anything.of())
                ),
                Rule.of(
                        RuleCondition.of(
                                BoolAnd.of(
                                        Builtins.is(KResult.class, Resolver.of("E1", KThing.class)),
                                        BoolNot.of(Builtins.is(KResult.class, Resolver.of("E2", KThing.class))))),
                        RuleCell.of(
                                "k",
                                Transform.of(
                                        Sequence.of(Variable.of("E1"), Terminal.of("<="), Variable.of("E2")),
                                        Resolver.of("E2", KThing.class),
                                        KItem.lessOrEqualsRightMissing(Resolver.of("E2", Aexp.class))),
                                Anything.of())
                ),
                Rule.of(
                        RuleCondition.of(Builtins.is(KResult.class, Resolver.of("R", KThing.class))),
                        RuleCell.of(
                                "k",
                                Transform.of(
                                        Arrays.asList(
                                                Variable.of("R"),
                                                Sequence.of(Terminal.of("E1"), Terminal.of("<="), Variable.of("[]"))),
                                        Bexp.less(Resolver.of("E1", Aexp.class), Resolver.of("R", Aexp.class))),
                                Anything.of())
                ),

                Rule.of(
                        RuleCondition.of(
                                BoolNot.of(Builtins.is(KResult.class, Resolver.of("E", KThing.class)))),
                        RuleCell.of(
                                "k",
                                Transform.of(
                                        Sequence.of(Terminal.of("!"), Variable.of("E")),
                                        Resolver.of("E", KThing.class),
                                        KItem.notMissingOperand()),
                                Anything.of())
                ),
                Rule.of(
                        RuleCondition.of(Builtins.is(KResult.class, Resolver.of("R", KThing.class))),
                        RuleCell.of(
                                "k",
                                Transform.of(
                                        Arrays.asList(
                                                Variable.of("R"),
                                                Sequence.of(Terminal.of("!"), Variable.of("[]"))),
                                        Bexp.not(Resolver.of("R", Bexp.class))),
                                Anything.of())
                ),

                Rule.of(
                        RuleCondition.of(
                                BoolNot.of(Builtins.is(KResult.class, Resolver.of("E", KThing.class)))),
                        RuleCell.of(
                                "k",
                                Transform.of(
                                        Sequence.of(Variable.of("Id"), Terminal.of("="), Variable.of("E"), Terminal.of(";")),
                                        Resolver.of("E", KThing.class),
                                        KItem.assignmentMissingOperand(Resolver.of("Id", KThing.class))),
                                Anything.of())
                ),
                Rule.of(
                        RuleCondition.of(Builtins.is(KResult.class, Resolver.of("R", KThing.class))),
                        RuleCell.of(
                                "k",
                                Transform.of(
                                        Arrays.asList(
                                                Variable.of("R"),
                                                Sequence.of(Terminal.of("!"), Variable.of("[]"))),
                                        Bexp.not(Resolver.of("R", Bexp.class))),
                                Anything.of())
                ),

                Rule.of(
                        RuleCondition.of(
                                BoolNot.of(Builtins.is(KResult.class, Resolver.of("C", KThing.class)))),
                        RuleCell.of(
                                "k",
                                Transform.of(
                                        Sequence.of(
                                                Terminal.of("if"), Terminal.of("("), Variable.of("C"), Terminal.of(")"),
                                                Variable.of("T"), Terminal.of("else"), Variable.of("E"),
                                                Terminal.of(";")),
                                        Resolver.of("E", KThing.class),
                                        KItem.ifMissingCondition(
                                                Resolver.of("T", KThing.class),
                                                Resolver.of("E", KThing.class))),
                                Anything.of())
                ),
                Rule.of(
                        RuleCondition.of(Builtins.is(KResult.class, Resolver.of("R", KThing.class))),
                        RuleCell.of(
                                "k",
                                Transform.of(
                                        Sequence.of(
                                                Terminal.of("if"), Terminal.of("("), Terminal.of("[]"), Terminal.of(")"),
                                                Variable.of("T"), Terminal.of("else"), Variable.of("E"),
                                                Terminal.of(";")),
                                        Bexp.not(Resolver.of("R", Bexp.class))),
                                Anything.of())
                ));
    }

    private static KThing loadProgram(String[] args) {
        /*
            int n, sum,
            n = 100,
            sum = 0,
            while (!(n <= 0)) {
                sum = sum + n,
                n = n + -1,
            }
        */
        return Pgm.of(
                Ids.of(Id.of("n"), Id.of("sum")),
                Stmt.sequence(
                        Stmt.assign(Id.of("n"), Aexp.aint(Int.of("100"))),
                        Stmt.sequence(
                                Stmt.assign(Id.of("sum"), Aexp.aint(Int.of("0"))),
                                Stmt.swhile(
                                        Bexp.not(Bexp.less(Aexp.id(Id.of("n")), Aexp.aint(Int.of("0")))),
                                        Block.stmt(
                                                Stmt.sequence(
                                                    Stmt.assign(
                                                            Id.of("sum"),
                                                            Aexp.add(Aexp.id(Id.of("sum")), Aexp.id(Id.of("n")))
                                                    ),
                                                    Stmt.assign(
                                                            Id.of("n"),
                                                            Aexp.add(Aexp.id(Id.of("n")), Aexp.aint(Int.of("-1")))
                                                    )
                                                )
                                        )
                                )
                        )
                )
        );
    }
}
