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
import com.runtimeverification.ktest.nonterminals.Pgm;
import com.runtimeverification.ktest.nonterminals.Stmt;
import com.runtimeverification.ktest.tools.KMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        KThing pgm = loadProgram();
        Configuration initialConfiguration = new Configuration()
                .replaceKCell(Cell.of("k", pgm, Attribute.of("color", "green")))
                .replaceStateCell(Cell.of("state", new KMap(), Attribute.of("color", "red")));
        List<Rule> rules = loadRules();
        System.out.println(run(initialConfiguration, rules));
    }

    private static Configuration run(Configuration current, List<Rule> rules) {
        while(true) {
            Optional<Configuration> next = step(current, rules);
            if (!next.isPresent()) {
                return current;
            }
            current = next.get();
        }
    }

    private static Optional<Configuration> step(Configuration configuration, List<Rule> rules) {
        if (configuration.getK().getTermCount() == 0) {
            return Optional.empty();
        }
        final KThing firstKCellItem = configuration.getK().getTerm(0);
        Optional<KThing> secondKCellItem = Optional.empty();
        if (configuration.getK().getTermCount() > 1) {
            secondKCellItem = Optional.of(configuration.getK().getTerm(1));
        }

        assert configuration.getState().getTermCount() == 1;
        final KThing stateCellItem = configuration.getState().getTerm(0);
        assert stateCellItem instanceof KMap;
        final KMap stateCellMap = (KMap) stateCellItem;

        for (Rule rule : rules) {
            Optional<Configuration> next = rule.apply(configuration, firstKCellItem, secondKCellItem, stateCellMap);
            if (next.isPresent()) {
                return next;
            }
        }
        return Optional.empty();
    }

    private static List<Rule> loadRules() {
        List<Rule> rules = new ArrayList<>();

        // Structural
        rules.add((configuration, firstKCellItem, secondKCellItem, stateCellMap) -> {
            if (firstKCellItem instanceof Block.EmptyBlock) {
                return Optional.of(configuration.replaceKCell(configuration.getK().removePrefix(1)));
            }
            return Optional.empty();
        });
        // Structural
        rules.add((configuration, firstKCellItem, secondKCellItem, stateCellMap) -> {
            if (firstKCellItem instanceof Block.StmtBlock) {
                Block.StmtBlock block = (Block.StmtBlock) firstKCellItem;
                return Optional.of(configuration.replaceKCell(
                        configuration.getK().replacePrefix(1, block.getStmt())));
            }
            return Optional.empty();
        });
        // Structural
        rules.add((configuration, firstKCellItem, secondKCellItem, stateCellMap) -> {
            if (firstKCellItem instanceof Stmt.BlockStmt) {
                Stmt.BlockStmt block = (Stmt.BlockStmt) firstKCellItem;
                return Optional.of(configuration.replaceKCell(
                        configuration.getK().replacePrefix(1, block.getBlock())));
            }
            return Optional.empty();
        });
        //Structural
        rules.add((configuration, firstKCellItem, secondKCellItem, stateCellMap) -> {
            if (firstKCellItem instanceof Stmt.SequenceStmt) {
                Stmt.SequenceStmt sequenceStmt = (Stmt.SequenceStmt) firstKCellItem;
                return Optional.of(configuration.replaceKCell(
                        configuration.getK().replacePrefix(
                                1, sequenceStmt.getFirst(), sequenceStmt.getSecond())));
            }
            return Optional.empty();
        });
        // Structural
        rules.add((configuration, firstKCellItem, secondKCellItem, stateCellMap) -> {
            if (firstKCellItem instanceof Stmt.WhileStmt) {
                Stmt.WhileStmt whileStmt = (Stmt.WhileStmt) firstKCellItem;
                return Optional.of(configuration.replaceKCell(
                        configuration.getK().replacePrefix(
                                1,
                                Stmt.sif(
                                        whileStmt.getCondition(),
                                        Block.stmt(Stmt.sequence(Stmt.block(whileStmt.getBlock()), whileStmt)),
                                        Block.empty()))));
            }
            return Optional.empty();
        });
        // Structural
        rules.add((configuration, firstKCellItem, secondKCellItem, stateCellMap) -> {
            if (firstKCellItem instanceof Pgm) {
                Pgm pgm = (Pgm) firstKCellItem;
                if (pgm.getIds() instanceof Ids.Empty) {
                    return Optional.of(
                            configuration.replaceKCell(configuration.getK().replacePrefix(1, pgm.getStmt())));
                }
            }
            return Optional.empty();
        });
        rules.add((configuration, firstKCellItem, secondKCellItem, stateCellMap) -> {
            if (firstKCellItem instanceof Aexp.IdAexp) {
                Aexp.IdAexp id = (Aexp.IdAexp) firstKCellItem;
                KThing value = stateCellMap.getMap().get(id.getId());
                if (value != null) {
                    return Optional.of(configuration.replaceKCell(
                            configuration.getK().replacePrefix(1, value)));
                }
            }
            return Optional.empty();
        });
        rules.add((configuration, firstKCellItem, secondKCellItem, stateCellMap) -> {
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
            return Optional.empty();
        });
        rules.add((configuration, firstKCellItem, secondKCellItem, stateCellMap) -> {
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
            return Optional.empty();
        });
        rules.add((configuration, firstKCellItem, secondKCellItem, stateCellMap) -> {
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
            return Optional.empty();
        });
        rules.add((configuration, firstKCellItem, secondKCellItem, stateCellMap) -> {
            if (firstKCellItem instanceof Bexp.NotBexp) {
                Bexp.NotBexp not = (Bexp.NotBexp) firstKCellItem;
                if ((not.getOperand() instanceof Bexp.BoolBexp)) {
                    Bexp.BoolBexp operand = (Bexp.BoolBexp) not.getOperand();
                    return Optional.of(configuration.replaceKCell(
                            configuration.getK().replacePrefix(1, Bexp.bool(Bool.of(
                                    !operand.getBool().getValue())))));
                }
            }
            return Optional.empty();
        });
        rules.add((configuration, firstKCellItem, secondKCellItem, stateCellMap) -> {
            if (firstKCellItem instanceof Bexp.AndBexp) {
                Bexp.AndBexp band = (Bexp.AndBexp) firstKCellItem;
                if ((band.getFirst() instanceof Bexp.BoolBexp)) {
                    Bexp.BoolBexp first = (Bexp.BoolBexp) band.getFirst();
                    if (first.getBool().getValue()) {
                        return Optional.of(configuration.replaceKCell(
                                configuration.getK().replacePrefix(1, band.getSecond())));
                    }
                }
            }
            return Optional.empty();
        });
        rules.add((configuration, firstKCellItem, secondKCellItem, stateCellMap) -> {
            if (firstKCellItem instanceof Bexp.AndBexp) {
                Bexp.AndBexp band = (Bexp.AndBexp) firstKCellItem;
                if ((band.getFirst() instanceof Bexp.BoolBexp)) {
                    Bexp.BoolBexp first = (Bexp.BoolBexp) band.getFirst();
                    if (!first.getBool().getValue()) {
                        return Optional.of(configuration.replaceKCell(
                                configuration.getK().replacePrefix(1, first)));
                    }
                }
            }
            return Optional.empty();
        });

        rules.add((configuration, firstKCellItem, secondKCellItem, stateCellMap) -> {
            if (firstKCellItem instanceof Stmt.AssignStmt) {
                Stmt.AssignStmt assign = (Stmt.AssignStmt) firstKCellItem;
                if (assign.getAexp() instanceof Aexp.IntAexp) {
                    Map<KThing, KThing> newMap = new HashMap<>(stateCellMap.getMap());
                    newMap.put(assign.getId(), assign.getAexp());
                    return Optional.of(
                            configuration
                                    .replaceKCell(configuration.getK().removePrefix(1))
                                    .replaceStateCell(
                                            configuration.getState().replacePrefix(1, new KMap(newMap))));
                }
            }
            return Optional.empty();
        });

        rules.add((configuration, firstKCellItem, secondKCellItem, stateCellMap) -> {
            if (firstKCellItem instanceof Stmt.IfStmt) {
                Stmt.IfStmt ifStmt = (Stmt.IfStmt) firstKCellItem;
                if (ifStmt.getCondition() instanceof Bexp.BoolBexp) {
                    Bexp.BoolBexp condition = (Bexp.BoolBexp) ifStmt.getCondition();
                    if (condition.getBool().getValue()) {
                        return Optional.of(configuration.replaceKCell(
                                configuration.getK().replacePrefix(1, ifStmt.getIthen())));
                    }
                }
            }
            return Optional.empty();
        });
        rules.add((configuration, firstKCellItem, secondKCellItem, stateCellMap) -> {
            if (firstKCellItem instanceof Stmt.IfStmt) {
                Stmt.IfStmt ifStmt = (Stmt.IfStmt) firstKCellItem;
                if (ifStmt.getCondition() instanceof Bexp.BoolBexp) {
                    Bexp.BoolBexp condition = (Bexp.BoolBexp) ifStmt.getCondition();
                    if (!condition.getBool().getValue()) {
                        return Optional.of(configuration.replaceKCell(
                                configuration.getK().replacePrefix(1, ifStmt.getIelse())));
                    }
                }
            }
            return Optional.empty();
        });
        rules.add((configuration, firstKCellItem, secondKCellItem, stateCellMap) -> {
            if (firstKCellItem instanceof Pgm) {
                Pgm pgm = (Pgm) firstKCellItem;
                if (pgm.getIds() instanceof Ids.NonEmpty) {
                    Ids.NonEmpty ids = (Ids.NonEmpty) pgm.getIds();
                    Map<KThing, KThing> newMap = new HashMap<>(stateCellMap.getMap());
                    newMap.put(ids.getId(), Aexp.aint(Int.of(0)));
                    return Optional.of(
                            configuration
                                    .replaceKCell(configuration.getK().replacePrefix(
                                            1, Pgm.of(ids.getTail(), pgm.getStmt())))
                                    .replaceStateCell(configuration.getState().replacePrefix(
                                            1, new KMap(newMap))));
                }
            }
            return Optional.empty();
        });

        rules.add((configuration, firstKCellItem, secondKCellItem, stateCellMap) -> {
            if (firstKCellItem instanceof Aexp.DivAexp) {
                Aexp.DivAexp div = (Aexp.DivAexp) firstKCellItem;
                if (!(div.getFirst() instanceof Aexp.IntAexp)) {
                    return Optional.of(configuration.replaceKCell(
                            configuration.getK().replacePrefix(
                                    1, div.getFirst(), KItem.divisionLeftMissing(div.getSecond()))));
                }
            }
            return Optional.empty();
        });
        rules.add((configuration, firstKCellItem, secondKCellItem, stateCellMap) -> {
            if (firstKCellItem instanceof Aexp.IntAexp) {
                if (secondKCellItem.isPresent()) {
                    if (secondKCellItem.get() instanceof KItem.DivisionLeftMissing) {
                        KItem.DivisionLeftMissing div = (KItem.DivisionLeftMissing) secondKCellItem.get();
                        return Optional.of(configuration.replaceKCell(
                                configuration.getK().replacePrefix(
                                        2, Aexp.div((Aexp) firstKCellItem, div.getRight()))));
                    }
                }
            }
            return Optional.empty();
        });
        rules.add((configuration, firstKCellItem, secondKCellItem, stateCellMap) -> {
            if (firstKCellItem instanceof Aexp.DivAexp) {
                Aexp.DivAexp div = (Aexp.DivAexp) firstKCellItem;
                if ((div.getFirst() instanceof Aexp.IntAexp) && !(div.getSecond() instanceof Aexp.IntAexp)) {
                    return Optional.of(configuration.replaceKCell(
                            configuration.getK().replacePrefix(
                                    1, div.getSecond(), KItem.divisionRightMissing(div.getFirst()))));
                }
            }
            return Optional.empty();
        });
        rules.add((configuration, firstKCellItem, secondKCellItem, stateCellMap) -> {
            if (firstKCellItem instanceof Aexp.IntAexp) {
                if (secondKCellItem.isPresent()) {
                    if (secondKCellItem.get() instanceof KItem.DivisionRightMissing) {
                        KItem.DivisionRightMissing div = (KItem.DivisionRightMissing) secondKCellItem.get();
                        return Optional.of(configuration.replaceKCell(
                                configuration.getK().replacePrefix(
                                        2, Aexp.div(div.getLeft(), (Aexp) firstKCellItem))));
                    }
                }
            }
            return Optional.empty();
        });

        rules.add((configuration, firstKCellItem, secondKCellItem, stateCellMap) -> {
            if (firstKCellItem instanceof Aexp.AddAexp) {
                Aexp.AddAexp add = (Aexp.AddAexp) firstKCellItem;
                if (!(add.getFirst() instanceof Aexp.IntAexp)) {
                    return Optional.of(configuration.replaceKCell(
                            configuration.getK().replacePrefix(
                                    1, add.getFirst(), KItem.additionLeftMissing(add.getSecond()))));
                }
            }
            return Optional.empty();
        });
        rules.add((configuration, firstKCellItem, secondKCellItem, stateCellMap) -> {
            if (firstKCellItem instanceof Aexp.IntAexp) {
                if (secondKCellItem.isPresent()) {
                    if (secondKCellItem.get() instanceof KItem.AdditionLeftMissing) {
                        KItem.AdditionLeftMissing add = (KItem.AdditionLeftMissing) secondKCellItem.get();
                        return Optional.of(configuration.replaceKCell(
                                configuration.getK().replacePrefix(
                                        2, Aexp.add((Aexp) firstKCellItem, add.getRight()))));
                    }
                }
            }
            return Optional.empty();
        });
        rules.add((configuration, firstKCellItem, secondKCellItem, stateCellMap) -> {
            if (firstKCellItem instanceof Aexp.AddAexp) {
                Aexp.AddAexp add = (Aexp.AddAexp) firstKCellItem;
                if ((add.getFirst() instanceof Aexp.IntAexp) && !(add.getSecond() instanceof Aexp.IntAexp)) {
                    return Optional.of(configuration.replaceKCell(
                            configuration.getK().replacePrefix(
                                    1, add.getSecond(), KItem.additionRightMissing(add.getFirst()))));
                }
            }
            return Optional.empty();
        });
        rules.add((configuration, firstKCellItem, secondKCellItem, stateCellMap) -> {
            if (firstKCellItem instanceof Aexp.IntAexp) {
                if (secondKCellItem.isPresent()) {
                    if (secondKCellItem.get() instanceof KItem.AdditionRightMissing) {
                        KItem.AdditionRightMissing add = (KItem.AdditionRightMissing) secondKCellItem.get();
                        return Optional.of(configuration.replaceKCell(
                                configuration.getK().replacePrefix(
                                        2, Aexp.add(add.getLeft(), (Aexp) firstKCellItem))));
                    }
                }
            }
            return Optional.empty();
        });

        rules.add((configuration, firstKCellItem, secondKCellItem, stateCellMap) -> {
            if (firstKCellItem instanceof Bexp.LessBexp) {
                Bexp.LessBexp less = (Bexp.LessBexp) firstKCellItem;
                if (!(less.getFirst() instanceof Aexp.IntAexp)) {
                    return Optional.of(configuration.replaceKCell(
                            configuration.getK().replacePrefix(
                                    1, less.getFirst(), KItem.lessOrEqualsLeftMissing(less.getSecond()))));
                }
            }
            return Optional.empty();
        });
        rules.add((configuration, firstKCellItem, secondKCellItem, stateCellMap) -> {
            if (firstKCellItem instanceof Aexp.IntAexp) {
                if (secondKCellItem.isPresent()) {
                    if (secondKCellItem.get() instanceof KItem.LessOrEqualsLeftMissing) {
                        KItem.LessOrEqualsLeftMissing less = (KItem.LessOrEqualsLeftMissing) secondKCellItem.get();
                        return Optional.of(configuration.replaceKCell(
                                configuration.getK().replacePrefix(
                                        2, Bexp.less((Aexp) firstKCellItem, less.getRight()))));
                    }
                }
            }
            return Optional.empty();
        });
        rules.add((configuration, firstKCellItem, secondKCellItem, stateCellMap) -> {
            if (firstKCellItem instanceof Bexp.LessBexp) {
                Bexp.LessBexp less = (Bexp.LessBexp) firstKCellItem;
                if ((less.getFirst() instanceof Aexp.IntAexp) && !(less.getSecond() instanceof Aexp.IntAexp)) {
                    return Optional.of(configuration.replaceKCell(
                            configuration.getK().replacePrefix(
                                    1, less.getSecond(), KItem.lessOrEqualsRightMissing(less.getFirst()))));
                }
            }
            return Optional.empty();
        });
        rules.add((configuration, firstKCellItem, secondKCellItem, stateCellMap) -> {
            if (firstKCellItem instanceof Aexp.IntAexp) {
                if (secondKCellItem.isPresent()) {
                    if (secondKCellItem.get() instanceof KItem.LessOrEqualsRightMissing) {
                        KItem.LessOrEqualsRightMissing less = (KItem.LessOrEqualsRightMissing) secondKCellItem.get();
                        return Optional.of(configuration.replaceKCell(
                                configuration.getK().replacePrefix(
                                        2, Bexp.less(less.getLeft(), (Aexp) firstKCellItem))));
                    }
                }
            }
            return Optional.empty();
        });

        rules.add((configuration, firstKCellItem, secondKCellItem, stateCellMap) -> {
            if (firstKCellItem instanceof Bexp.NotBexp) {
                Bexp.NotBexp not = (Bexp.NotBexp) firstKCellItem;
                if (!(not.getOperand() instanceof Bexp.BoolBexp)) {
                    return Optional.of(configuration.replaceKCell(
                            configuration.getK().replacePrefix(
                                    1, not.getOperand(), KItem.notMissingOperand())));
                }
            }
            return Optional.empty();
        });
        rules.add((configuration, firstKCellItem, secondKCellItem, stateCellMap) -> {
            if (firstKCellItem instanceof Bexp.BoolBexp) {
                if (secondKCellItem.isPresent()) {
                    if (secondKCellItem.get() instanceof KItem.NotMissingOperand) {
                        return Optional.of(configuration.replaceKCell(
                                configuration.getK().replacePrefix(
                                        2, Bexp.not((Bexp) firstKCellItem))));
                    }
                }
            }
            return Optional.empty();
        });

        rules.add((configuration, firstKCellItem, secondKCellItem, stateCellMap) -> {
            if (firstKCellItem instanceof Stmt.AssignStmt) {
                Stmt.AssignStmt assign = (Stmt.AssignStmt) firstKCellItem;
                if (!(assign.getAexp() instanceof Aexp.IntAexp)) {
                    return Optional.of(configuration.replaceKCell(
                            configuration.getK().replacePrefix(
                                    1, assign.getAexp(), KItem.assignmentMissingOperand(assign.getId()))));
                }
            }
            return Optional.empty();
        });
        rules.add((configuration, firstKCellItem, secondKCellItem, stateCellMap) -> {
            if (firstKCellItem instanceof Aexp.IntAexp) {
                if (secondKCellItem.isPresent()) {
                    if (secondKCellItem.get() instanceof KItem.AssignmentMissingOperand) {
                        KItem.AssignmentMissingOperand assignment =
                                (KItem.AssignmentMissingOperand) secondKCellItem.get();
                        return Optional.of(configuration.replaceKCell(
                                configuration.getK().replacePrefix(
                                        2, Stmt.assign(assignment.getId(), (Aexp) firstKCellItem))));
                    }
                }
            }
            return Optional.empty();
        });

        rules.add((configuration, firstKCellItem, secondKCellItem, stateCellMap) -> {
            if (firstKCellItem instanceof Stmt.IfStmt) {
                Stmt.IfStmt sif = (Stmt.IfStmt) firstKCellItem;
                if (!(sif.getCondition() instanceof Bexp.BoolBexp)) {
                    return Optional.of(configuration.replaceKCell(
                            configuration.getK().replacePrefix(
                                    1,
                                    sif.getCondition(),
                                    KItem.ifMissingCondition(sif.getIthen(), sif.getIelse()))));
                }
            }
            return Optional.empty();
        });
        rules.add((configuration, firstKCellItem, secondKCellItem, stateCellMap) -> {
            if (firstKCellItem instanceof Bexp.BoolBexp) {
                if (secondKCellItem.isPresent()) {
                    if (secondKCellItem.get() instanceof KItem.IfMissingCondition) {
                        KItem.IfMissingCondition sif = (KItem.IfMissingCondition) secondKCellItem.get();
                        return Optional.of(configuration.replaceKCell(
                                configuration.getK().replacePrefix(
                                        2, Stmt.sif((Bexp) firstKCellItem, sif.getIthen(), sif.getIelse()))));
                    }
                }
            }
            return Optional.empty();
        });
        return rules;
    }

    private static KThing loadProgram() {
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
