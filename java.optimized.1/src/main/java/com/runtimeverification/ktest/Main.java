package com.runtimeverification.ktest;

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
import com.runtimeverification.ktest.optimizations.Pair;
import com.runtimeverification.ktest.optimizations.RuleIndexes;
import com.runtimeverification.ktest.optimizations.State;
import com.runtimeverification.ktest.tools.KMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        KThing pgm = loadProgram();
        Configuration initialConfiguration = new Configuration()
                .replaceKCell(Cell.of(pgm))
                .replaceStateCell(Cell.of(new KMap()));
        RuleIndexes ruleIndexes = new RuleIndexes();
        List<Rule> rules = loadRules(ruleIndexes);

        /*
        for (int i = 0; i < 10000; i++) {
            run(initialConfiguration, rules);
        }
        System.out.println(run(initialConfiguration, rules));
        */

        Map<State, List<Pair<Integer, State>>> compiledRules = compileRules(ruleIndexes);

        for (int i = 0; i < 10000; i++) {
            compiledRun(initialConfiguration, rules, compiledRules);
        }
        System.out.println(compiledRun(initialConfiguration, rules, compiledRules));
    }

    private static Configuration compiledRun(
            Configuration current, List<Rule> rules, Map<State, List<Pair<Integer, State>>> compiledRules) {
        State state = State.START;
        while(true) {
            Pair<State, Optional<Configuration>> next = compiledStep(current, rules, compiledRules, state);
             if (!next.getSecond().isPresent()) {
                assert next.getFirst() == State.END;
                return current;
            }
            current = next.getSecond().get();
            state = next.getFirst();
        }
    }

    private static Pair<State, Optional<Configuration>> compiledStep(
            Configuration configuration,
            List<Rule> rules,
            Map<State, List<Pair<Integer, State>>> compiledRules,
            State state) {
        if (configuration.getK().getTermCount() == 0) {
            return new Pair<>(state, Optional.empty());
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

        for (Pair<Integer, State> ruleIndex : compiledRules.get(state)) {
            Rule rule = rules.get(ruleIndex.getFirst());
            Optional<Configuration> next = rule.apply(configuration, firstKCellItem, secondKCellItem, stateCellMap);
            if (next.isPresent()) {
                return new Pair<>(ruleIndex.getSecond(), next);
            }
        }
        return new Pair<>(state, Optional.empty());
    }

    private static Map<State, List<Pair<Integer, State>>> compileRules(RuleIndexes ruleIndexes) {
        Map<State, List<Pair<Integer, State>>> compiledRules = new HashMap<>();
        compiledRules.put(State.START, Arrays.asList(
                new Pair<>(ruleIndexes.idDeclaration, State.START),
                new Pair<>(ruleIndexes.programToStatement, State.SEQUENCE_STATEMENT_1)
        ));
        compiledRules.put(State.SEQUENCE_STATEMENT_1, Arrays.asList(
                new Pair<>(ruleIndexes.sequenceResolve, State.ASSIGNMENT_1)
        ));
        compiledRules.put(State.ASSIGNMENT_1, Arrays.asList(
                new Pair<>(ruleIndexes.assignmentResolve, State.SEQUENCE_STATEMENT_2)
        ));
        compiledRules.put(State.SEQUENCE_STATEMENT_2, Arrays.asList(
                new Pair<>(ruleIndexes.sequenceResolve, State.ASSIGNMENT_2)
        ));
        compiledRules.put(State.ASSIGNMENT_2, Arrays.asList(
                new Pair<>(ruleIndexes.assignmentResolve, State.SWHILE)
        ));
        compiledRules.put(State.SWHILE, Arrays.asList(
                new Pair<>(ruleIndexes.whileToIf, State.WHILE_IF)
        ));
        compiledRules.put(State.WHILE_IF, Arrays.asList(
                new Pair<>(ruleIndexes.ifUnpacking, State.WHILE_IF_CONDITION)
        ));
        compiledRules.put(State.WHILE_IF_CONDITION, Arrays.asList(
                new Pair<>(ruleIndexes.notUnpacking, State.WHILE_IF_CONDITION_LESS)
        ));
        compiledRules.put(State.WHILE_IF_CONDITION_LESS, Arrays.asList(
                new Pair<>(ruleIndexes.lessOrEqualsLeftUnpacking, State.WHILE_IF_CONDITION_LESS_LEFT)
        ));
        compiledRules.put(State.WHILE_IF_CONDITION_LESS_LEFT, Arrays.asList(
                new Pair<>(ruleIndexes.idResolve, State.WHILE_IF_CONDITION_LESS_LEFT_RESOLVED)
        ));
        compiledRules.put(State.WHILE_IF_CONDITION_LESS_LEFT_RESOLVED, Arrays.asList(
                new Pair<>(ruleIndexes.lessOrEqualsLeftRepacking, State.WHILE_IF_CONDITION_LESS_LEFT_REPACKED)
        ));
        compiledRules.put(State.WHILE_IF_CONDITION_LESS_LEFT_REPACKED, Arrays.asList(
                new Pair<>(ruleIndexes.lessOrEqualsResolve, State.WHILE_IF_CONDITION_LESS_RESOLVED)
        ));
        compiledRules.put(State.WHILE_IF_CONDITION_LESS_RESOLVED, Arrays.asList(
                new Pair<>(ruleIndexes.notRepacking, State.WHILE_IF_CONDITION_REPACKED)
        ));
        compiledRules.put(State.WHILE_IF_CONDITION_REPACKED, Arrays.asList(
                new Pair<>(ruleIndexes.notResolve, State.WHILE_IF_CONDITION_RESOLVED)
        ));
        compiledRules.put(State.WHILE_IF_CONDITION_RESOLVED, Arrays.asList(
                new Pair<>(ruleIndexes.ifRepacking, State.WHILE_IF_REPACKED)
        ));
        compiledRules.put(State.WHILE_IF_REPACKED, Arrays.asList(
                new Pair<>(ruleIndexes.ifResolveTrue, State.WHILE_IF_THEN),
                new Pair<>(ruleIndexes.ifResolveFalse, State.WHILE_IF_ELSE)
        ));
        compiledRules.put(State.WHILE_IF_ELSE, Arrays.asList(
                new Pair<>(ruleIndexes.emptyBlockRemoval, State.END)
        ));
        compiledRules.put(State.WHILE_IF_THEN, Arrays.asList(
                new Pair<>(ruleIndexes.blockToStatement, State.WHILE_IF_STATEMENT)
        ));
        compiledRules.put(State.WHILE_IF_STATEMENT, Arrays.asList(
                new Pair<>(ruleIndexes.sequenceResolve, State.WHILE_BLOCK_STATEMENT)
        ));
        compiledRules.put(State.WHILE_BLOCK_STATEMENT, Arrays.asList(
                new Pair<>(ruleIndexes.statementToBlock, State.WHILE_BLOCK)
        ));
        compiledRules.put(State.WHILE_BLOCK, Arrays.asList(
                new Pair<>(ruleIndexes.blockToStatement, State.WHILE_STATEMENT)
        ));
        compiledRules.put(State.WHILE_STATEMENT, Arrays.asList(
                new Pair<>(ruleIndexes.sequenceResolve, State.WHILE_ASSIGNMENT_1)
        ));

        compiledRules.put(State.WHILE_ASSIGNMENT_1, Arrays.asList(
                new Pair<>(ruleIndexes.assignmentUnpacking, State.WHILE_ASSIGNMENT_1_VALUE)
        ));
        compiledRules.put(State.WHILE_ASSIGNMENT_1_VALUE, Arrays.asList(
                new Pair<>(ruleIndexes.additionLeftUnpacking, State.WHILE_ASSIGNMENT_1_VALUE_LEFT)
        ));
        compiledRules.put(State.WHILE_ASSIGNMENT_1_VALUE_LEFT, Arrays.asList(
                new Pair<>(ruleIndexes.idResolve, State.WHILE_ASSIGNMENT_1_VALUE_LEFT_RESOLVED)
        ));
        compiledRules.put(State.WHILE_ASSIGNMENT_1_VALUE_LEFT_RESOLVED, Arrays.asList(
                new Pair<>(ruleIndexes.additionLeftRepacking, State.WHILE_ASSIGNMENT_1_VALUE_LEFT_REPACKED)
        ));
        compiledRules.put(State.WHILE_ASSIGNMENT_1_VALUE_LEFT_REPACKED, Arrays.asList(
                new Pair<>(ruleIndexes.additionRightUnpacking, State.WHILE_ASSIGNMENT_1_VALUE_RIGHT)
        ));
        compiledRules.put(State.WHILE_ASSIGNMENT_1_VALUE_RIGHT, Arrays.asList(
                new Pair<>(ruleIndexes.idResolve, State.WHILE_ASSIGNMENT_1_VALUE_RIGHT_RESOLVED)
        ));
        compiledRules.put(State.WHILE_ASSIGNMENT_1_VALUE_RIGHT_RESOLVED, Arrays.asList(
                new Pair<>(ruleIndexes.additionRightRepacking, State.WHILE_ASSIGNMENT_1_VALUE_RIGHT_REPACKED)
        ));
        compiledRules.put(State.WHILE_ASSIGNMENT_1_VALUE_RIGHT_REPACKED, Arrays.asList(
                new Pair<>(ruleIndexes.addResolve, State.WHILE_ASSIGNMENT_1_VALUE_RESOLVED)
        ));
        compiledRules.put(State.WHILE_ASSIGNMENT_1_VALUE_RESOLVED, Arrays.asList(
                new Pair<>(ruleIndexes.assignmentRepacking, State.WHILE_ASSIGNMENT_1_REPACKED)
        ));
        compiledRules.put(State.WHILE_ASSIGNMENT_1_REPACKED, Arrays.asList(
                new Pair<>(ruleIndexes.assignmentResolve, State.WHILE_ASSIGNMENT_2)
        ));

        compiledRules.put(State.WHILE_ASSIGNMENT_2, Arrays.asList(
                new Pair<>(ruleIndexes.assignmentUnpacking, State.WHILE_ASSIGNMENT_2_VALUE)
        ));
        compiledRules.put(State.WHILE_ASSIGNMENT_2_VALUE, Arrays.asList(
                new Pair<>(ruleIndexes.additionLeftUnpacking, State.WHILE_ASSIGNMENT_2_VALUE_LEFT)
        ));
        compiledRules.put(State.WHILE_ASSIGNMENT_2_VALUE_LEFT, Arrays.asList(
                new Pair<>(ruleIndexes.idResolve, State.WHILE_ASSIGNMENT_2_VALUE_LEFT_RESOLVED)
        ));
        compiledRules.put(State.WHILE_ASSIGNMENT_2_VALUE_LEFT_RESOLVED, Arrays.asList(
                new Pair<>(ruleIndexes.additionLeftRepacking, State.WHILE_ASSIGNMENT_2_VALUE_LEFT_REPACKED)
        ));
        compiledRules.put(State.WHILE_ASSIGNMENT_2_VALUE_LEFT_REPACKED, Arrays.asList(
                new Pair<>(ruleIndexes.addResolve, State.WHILE_ASSIGNMENT_2_VALUE_RESOLVED)
        ));
        compiledRules.put(State.WHILE_ASSIGNMENT_2_VALUE_RESOLVED, Arrays.asList(
                new Pair<>(ruleIndexes.assignmentRepacking, State.WHILE_ASSIGNMENT_2_REPACKED)
        ));
        compiledRules.put(State.WHILE_ASSIGNMENT_2_REPACKED, Arrays.asList(
                new Pair<>(ruleIndexes.assignmentResolve, State.SWHILE)
        ));

        return compiledRules;
    }
    /*
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
    */



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

    private static List<Rule> loadRules(RuleIndexes ruleIndexes) {
        List<Rule> rules = new ArrayList<>();

        // Structural
        ruleIndexes.emptyBlockRemoval = rules.size();
        rules.add((configuration, firstKCellItem, secondKCellItem, stateCellMap) -> {
            if (firstKCellItem instanceof Block.EmptyBlock) {
                return Optional.of(configuration.replaceKCell(configuration.getK().removePrefix(1)));
            }
            return Optional.empty();
        });
        // Structural
        ruleIndexes.blockToStatement = rules.size();
        rules.add((configuration, firstKCellItem, secondKCellItem, stateCellMap) -> {
            if (firstKCellItem instanceof Block.StmtBlock) {
                Block.StmtBlock block = (Block.StmtBlock) firstKCellItem;
                return Optional.of(configuration.replaceKCell(
                        configuration.getK().replacePrefix(1, block.getStmt())));
            }
            return Optional.empty();
        });
        // Structural
        ruleIndexes.statementToBlock = rules.size();
        rules.add((configuration, firstKCellItem, secondKCellItem, stateCellMap) -> {
            if (firstKCellItem instanceof Stmt.BlockStmt) {
                Stmt.BlockStmt block = (Stmt.BlockStmt) firstKCellItem;
                return Optional.of(configuration.replaceKCell(
                        configuration.getK().replacePrefix(1, block.getBlock())));
            }
            return Optional.empty();
        });
        //Structural
        ruleIndexes.sequenceResolve = rules.size();
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
        ruleIndexes.whileToIf = rules.size();
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
        ruleIndexes.programToStatement = rules.size();
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
        ruleIndexes.idResolve = rules.size();
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
        ruleIndexes.divisionResolve = rules.size();
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
        ruleIndexes.addResolve = rules.size();
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
        ruleIndexes.lessOrEqualsResolve = rules.size();
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
        ruleIndexes.notResolve = rules.size();
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
        ruleIndexes.andLeftResolveTrue = rules.size();
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
        ruleIndexes.andLeftResolveFalse = rules.size();
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

        ruleIndexes.assignmentResolve = rules.size();
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

        ruleIndexes.ifResolveTrue = rules.size();
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
        ruleIndexes.ifResolveFalse = rules.size();
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
        ruleIndexes.idDeclaration = rules.size();
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

        ruleIndexes.divisionLeftUnpacking = rules.size();
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
        ruleIndexes.divisionLeftRepacking = rules.size();
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
        ruleIndexes.divisionRightUnpacking = rules.size();
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
        ruleIndexes.divisionRightRepacking = rules.size();
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

        ruleIndexes.additionLeftUnpacking = rules.size();
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
        ruleIndexes.additionLeftRepacking = rules.size();
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
        ruleIndexes.additionRightUnpacking = rules.size();
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
        ruleIndexes.additionRightRepacking = rules.size();
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

        ruleIndexes.lessOrEqualsLeftUnpacking = rules.size();
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
        ruleIndexes.lessOrEqualsLeftRepacking = rules.size();
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
        ruleIndexes.lessOrEqualsRightUnpacking = rules.size();
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
        ruleIndexes.lessOrEqualsRightRepacking = rules.size();
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

        ruleIndexes.notUnpacking = rules.size();
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
        ruleIndexes.notRepacking = rules.size();
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

        ruleIndexes.assignmentUnpacking = rules.size();
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
        ruleIndexes.assignmentRepacking = rules.size();
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

        ruleIndexes.ifUnpacking = rules.size();
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
        ruleIndexes.ifRepacking = rules.size();
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
