package com.runtimeverification.ktest;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        KThing pgm = loadProgram();
        RuleIndexes ruleIndexes = new RuleIndexes();
        List<Rule> rules = loadRules(ruleIndexes);

        /*
        for (int i = 0; i < 10000; i++) {
            run(initialConfiguration, rules);
        }
        System.out.println(run(initialConfiguration, rules));
        */

        Map<State, List<Pair<List<Integer>, State>>> compiledRules = compileRules(ruleIndexes);

        /*
        for (int i = 0; i < 100000; i++) {
            Configuration initialConfiguration = new Configuration(pgm, new HashMap<>());
            compiledRun(initialConfiguration, rules, compiledRules);
        }
        */
        Configuration initialConfiguration = new Configuration(pgm, new HashMap<>());
        System.out.println(compiledRun(initialConfiguration, rules, compiledRules));
    }

    private static Configuration compiledRun(
            Configuration current, List<Rule> rules, Map<State, List<Pair<List<Integer>, State>>> compiledRules) {
        State state = State.START;
        while(true) {
            State nextState = compiledStep(current, rules, compiledRules, state);
            if (nextState == State.END || nextState == State.ERROR) {
                assert nextState == State.END;
                return current;
            }
            state = nextState;
        }
    }

    private static State compiledStep(
            Configuration configuration,
            List<Rule> rules,
            Map<State, List<Pair<List<Integer>, State>>> compiledRules,
            State state) {
        for (Pair<List<Integer>, State> ruleIndexes : compiledRules.getOrDefault(state, Collections.emptyList())) {
            boolean firstIteration = true;
            for (int index : ruleIndexes.getFirst()) {
                if (configuration.getKSize() == 0) {
                    return State.ERROR;
                }
                Rule rule = rules.get(index);
                if (!rule.apply(configuration)) {
                    if (firstIteration) {
                        break;
                    }
                    return State.ERROR;
                }
                firstIteration = false;
            }
            if (!firstIteration) {
                return ruleIndexes.getSecond();
            }
        }
        return State.ERROR;
    }

    private static Map<State, List<Pair<List<Integer>, State>>> compileRules(RuleIndexes ruleIndexes) {
        Map<State, List<Pair<List<Integer>, State>>> compiledRules = new HashMap<>();
        compiledRules.put(State.START, Arrays.asList(
                new Pair<>(Arrays.asList(ruleIndexes.idDeclaration), State.START),
                new Pair<>(
                        Arrays.asList(
                                ruleIndexes.programToStatement,
                                ruleIndexes.sequenceResolve,
                                ruleIndexes.assignmentResolve,
                                ruleIndexes.sequenceResolve,
                                ruleIndexes.assignmentResolve),
                        State.SWHILE)));
        compiledRules.put(State.SWHILE, Arrays.asList(
                new Pair<>(
                        Arrays.asList(
                                ruleIndexes.whileToIf,
                                ruleIndexes.ifUnpacking,
                                ruleIndexes.notUnpacking,
                                ruleIndexes.lessOrEqualsLeftUnpacking,
                                ruleIndexes.idResolve,
                                ruleIndexes.lessOrEqualsLeftRepacking,
                                ruleIndexes.lessOrEqualsResolve,
                                ruleIndexes.notRepacking,
                                ruleIndexes.notResolve,
                                ruleIndexes.ifRepacking),
                        State.WHILE_IF_REPACKED)
        ));
        compiledRules.put(State.WHILE_IF_REPACKED, Arrays.asList(
                new Pair<>(
                        Arrays.asList(
                                ruleIndexes.ifResolveTrue,
                                ruleIndexes.sequenceResolve,
                                ruleIndexes.sequenceResolve,
                                ruleIndexes.assignmentUnpacking,
                                ruleIndexes.additionLeftUnpacking,
                                ruleIndexes.idResolve,
                                ruleIndexes.additionLeftRepacking,
                                ruleIndexes.additionRightUnpacking,
                                ruleIndexes.idResolve,
                                ruleIndexes.additionRightRepacking,
                                ruleIndexes.addResolve,
                                ruleIndexes.assignmentRepacking,
                                ruleIndexes.assignmentResolve,
                                ruleIndexes.assignmentUnpacking,
                                ruleIndexes.additionLeftUnpacking,
                                ruleIndexes.idResolve,
                                ruleIndexes.additionLeftRepacking,
                                ruleIndexes.addResolve,
                                ruleIndexes.assignmentRepacking,
                                ruleIndexes.assignmentResolve),
                        State.SWHILE),
                new Pair<>(
                        Arrays.asList(
                                ruleIndexes.ifResolveFalse,
                                ruleIndexes.emptyBlockRemoval),
                        State.END)
        ));

        return compiledRules;
    }
    /*
                                Stmt.swhile(
                                        Bexp.not(Bexp.lessOrEquals(Aexp.id(Id.of("n")), Aexp.aint(Int.of("0")))),
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


    private static List<Rule> loadRules(RuleIndexes ruleIndexes) {
        List<Rule> rules = new ArrayList<>();

        // Structural
        ruleIndexes.emptyBlockRemoval = rules.size();
        rules.add((configuration) -> {
            final KThing firstKCellItem = configuration.getKLastElement();
            if (firstKCellItem.type == KThing.Type.BLOCK_EMPTY) {
                configuration.replaceKPrefix(1);
                return true;
            }
            return false;
        });
        // Structural
        ruleIndexes.blockToStatement = rules.size();
        rules.add((configuration) -> {
            final KThing firstKCellItem = configuration.getKLastElement();
            if (firstKCellItem.type == KThing.Type.BLOCK_STMT) {
                configuration.replaceKPrefix(1, firstKCellItem.child1);
                return true;
            }
            return false;
        });
        // Structural
        ruleIndexes.statementToBlock = rules.size();
        rules.add((configuration) -> {
            final KThing firstKCellItem = configuration.getKLastElement();
            if (firstKCellItem.type == KThing.Type.STMT_BLOCK) {
                configuration.replaceKPrefix(1, firstKCellItem.child1);
                return true;
            }
            return false;
        });
        //Structural
        ruleIndexes.sequenceResolve = rules.size();
        rules.add((configuration) -> {
            final KThing firstKCellItem = configuration.getKLastElement();
            if (firstKCellItem.type == KThing.Type.STMT_SEQUENCE) {
                configuration.replaceKPrefix(1, firstKCellItem.child1, firstKCellItem.child2);
                return true;
            }
            return false;
        });
        // Structural
        ruleIndexes.whileToIf = rules.size();
        rules.add((configuration) -> {
            final KThing firstKCellItem = configuration.getKLastElement();
            if (firstKCellItem.type == KThing.Type.STMT_WHILE) {
                configuration.replaceKPrefix(
                                1,
                                Stmt.sif(
                                        firstKCellItem.child1,
                                        Stmt.sequence(firstKCellItem.child2.child1, firstKCellItem),
                                        Block.empty()));
                return true;
            }
            return false;
        });
        // Structural
        ruleIndexes.programToStatement = rules.size();
        rules.add((configuration) -> {
            final KThing firstKCellItem = configuration.getKLastElement();
            if (firstKCellItem.type == KThing.Type.PGM) {
                if (firstKCellItem.child1.type == KThing.Type.IDS_EMPTY) {
                    configuration.replaceKPrefix(1, firstKCellItem.child2);
                    return true;
                }
            }
            return false;
        });
        ruleIndexes.idResolve = rules.size();
        rules.add((configuration) -> {
            final KThing firstKCellItem = configuration.getKLastElement();
            if (firstKCellItem.type == KThing.Type.AEXP_ID) {
                KThing value = configuration.getState().get(firstKCellItem.child1);
                if (value != null) {
                    configuration.replaceKPrefix(1, value);
                    return true;
                }
            }
            return false;
        });
        ruleIndexes.divisionResolve = rules.size();
        rules.add((configuration) -> {
            final KThing firstKCellItem = configuration.getKLastElement();
            if (firstKCellItem.type == KThing.Type.AEXP_DIV) {
                KThing first = firstKCellItem.child1;
                KThing second = firstKCellItem.child2;
                if ((first.type == KThing.Type.AEXP_INT) && (second.type == KThing.Type.AEXP_INT)) {
                    int secondValue = second.child1.getAsInt();
                    if (secondValue != 0) {
                        configuration.replaceKPrefix(1, Aexp.aint(Int.of(
                                        first.child1.getAsInt() / secondValue)));
                        return true;
                    }
                }
            }
            return false;
        });
        ruleIndexes.addResolve = rules.size();
        rules.add((configuration) -> {
            final KThing firstKCellItem = configuration.getKLastElement();
            if (firstKCellItem.type == KThing.Type.AEXP_ADD) {
                KThing first = firstKCellItem.child1;
                KThing second = firstKCellItem.child2;
                if ((first.type == KThing.Type.AEXP_INT) && (second.type == KThing.Type.AEXP_INT)) {
                    configuration.replaceKPrefix(1, Aexp.aint(Int.of(
                                    first.child1.getAsInt() + second.child1.getAsInt())));
                    return true;
                }
            }
            return false;
        });
        ruleIndexes.lessOrEqualsResolve = rules.size();
        rules.add((configuration) -> {
            final KThing firstKCellItem = configuration.getKLastElement();
            if (firstKCellItem.type == KThing.Type.BEXP_LESS_OR_EQUALS) {
                KThing first = firstKCellItem.child1;
                KThing second = firstKCellItem.child2;
                if ((first.type == KThing.Type.AEXP_INT) && (second.type == KThing.Type.AEXP_INT)) {
                    configuration.replaceKPrefix(1, Bexp.bool(Bool.of(
                            first.child1.getAsInt() <= second.child1.getAsInt())));
                    return true;
                }
            }
            return false;
        });
        ruleIndexes.notResolve = rules.size();
        rules.add((configuration) -> {
            final KThing firstKCellItem = configuration.getKLastElement();
            if (firstKCellItem.type == KThing.Type.BEXP_NOT) {
                KThing operand = firstKCellItem.child1;
                if (operand.type == KThing.Type.BEXP_BOOL) {
                    configuration.replaceKPrefix(1, Bexp.bool(Bool.of(
                                    !operand.child1.getAsBool())));
                    return true;
                }
            }
            return false;
        });
        ruleIndexes.andLeftResolveTrue = rules.size();
        rules.add((configuration) -> {
            final KThing firstKCellItem = configuration.getKLastElement();
            if (firstKCellItem.type == KThing.Type.BEXP_NOT) {
                KThing first = firstKCellItem.child1;
                if (first.type == KThing.Type.BEXP_BOOL) {
                    if (first.child1.getAsBool()) {
                        configuration.replaceKPrefix(1, firstKCellItem.child2);
                        return true;
                    }
                }
            }
            return false;
        });
        ruleIndexes.andLeftResolveFalse = rules.size();
        rules.add((configuration) -> {
            final KThing firstKCellItem = configuration.getKLastElement();
            if (firstKCellItem.type == KThing.Type.BEXP_NOT) {
                KThing first = firstKCellItem.child1;
                if (first.type == KThing.Type.BEXP_BOOL) {
                    if (!first.child1.getAsBool()) {
                        configuration.replaceKPrefix(1, first);
                        return true;
                    }
                }
            }
            return false;
        });

        ruleIndexes.assignmentResolve = rules.size();
        rules.add((configuration) -> {
            final KThing firstKCellItem = configuration.getKLastElement();
            if (firstKCellItem.type == KThing.Type.STMT_ASSIGN) {
                KThing second = firstKCellItem.child2;
                if (second.type == KThing.Type.AEXP_INT) {
                    configuration.getState().put(firstKCellItem.child1, second);
                    configuration.replaceKPrefix(1);
                    return true;
                }
            }
            return false;
        });

        ruleIndexes.ifResolveTrue = rules.size();
        rules.add((configuration) -> {
            final KThing firstKCellItem = configuration.getKLastElement();
            if (firstKCellItem.type == KThing.Type.STMT_IF) {
                KThing condition = firstKCellItem.child1;
                if (condition.type == KThing.Type.BEXP_BOOL && condition.child1.getAsBool()) {
                    configuration.replaceKPrefix(1, firstKCellItem.child2);
                    return true;
                }
            }
            return false;
        });
        ruleIndexes.ifResolveFalse = rules.size();
        rules.add((configuration) -> {
            final KThing firstKCellItem = configuration.getKLastElement();
            if (firstKCellItem.type == KThing.Type.STMT_IF) {
                KThing condition = firstKCellItem.child1;
                if (condition.type == KThing.Type.BEXP_BOOL && !condition.child1.getAsBool()) {
                    configuration.replaceKPrefix(1, firstKCellItem.child3);
                    return true;
                }
            }
            return false;
        });
        ruleIndexes.idDeclaration = rules.size();
        rules.add((configuration) -> {
            final KThing firstKCellItem = configuration.getKLastElement();
            if (firstKCellItem.type == KThing.Type.PGM) {
                KThing ids = firstKCellItem.child1;
                if (ids.type == KThing.Type.IDS_NON_EMPTY) {
                    configuration.getState().put(ids.child1, Aexp.aint(Int.of(0)));
                    configuration.replaceKPrefix(1, Pgm.of(ids.child2, firstKCellItem.child2));
                    return true;
                }
            }
            return false;
        });

        ruleIndexes.divisionLeftUnpacking = rules.size();
        rules.add((configuration) -> {
            final KThing firstKCellItem = configuration.getKLastElement();
            if (firstKCellItem.type == KThing.Type.AEXP_DIV) {
                KThing first = firstKCellItem.child1;
                if (!(first.type == KThing.Type.AEXP_INT)) {
                    configuration.replaceKPrefix(
                                    1, first, KItem.divisionLeftMissing(firstKCellItem.child2));
                    return true;
                }
            }
            return false;
        });
        ruleIndexes.divisionLeftRepacking = rules.size();
        rules.add((configuration) -> {
            final KThing firstKCellItem = configuration.getKLastElement();
            if (firstKCellItem.type == KThing.Type.AEXP_INT) {
                if (configuration.getKSize() > 1) {
                    KThing div = configuration.getKElementBeforeLast();
                    if (div.type == KThing.Type.KITEM_DIVISION_LEFT_MISSING) {
                        configuration.replaceKPrefix(
                                        2, Aexp.div(firstKCellItem, div.child1));
                        return true;
                    }
                }
            }
            return false;
        });
        ruleIndexes.divisionRightUnpacking = rules.size();
        rules.add((configuration) -> {
            final KThing firstKCellItem = configuration.getKLastElement();
            if (firstKCellItem.type == KThing.Type.AEXP_DIV) {
                KThing first = firstKCellItem.child1;
                KThing second = firstKCellItem.child2;
                if ((first.type == KThing.Type.AEXP_INT) && !(second.type == KThing.Type.AEXP_INT)) {
                    configuration.replaceKPrefix(1, second, KItem.divisionRightMissing(first));
                    return true;
                }
            }
            return false;
        });
        ruleIndexes.divisionRightRepacking = rules.size();
        rules.add((configuration) -> {
            final KThing firstKCellItem = configuration.getKLastElement();
            if (firstKCellItem.type == KThing.Type.AEXP_INT) {
                if (configuration.getKSize() > 1) {
                    KThing div = configuration.getKElementBeforeLast();
                    if (div.type == KThing.Type.KITEM_DIVISION_RIGHT_MISSING) {
                        configuration.replaceKPrefix(2, Aexp.div(div.child1, firstKCellItem));
                        return true;
                    }
                }
            }
            return false;
        });

        ruleIndexes.additionLeftUnpacking = rules.size();
        rules.add((configuration) -> {
            final KThing firstKCellItem = configuration.getKLastElement();
            if (firstKCellItem.type == KThing.Type.AEXP_ADD) {
                KThing first = firstKCellItem.child1;
                if (!(first.type == KThing.Type.AEXP_INT)) {
                    configuration.replaceKPrefix(1, first, KItem.additionLeftMissing(firstKCellItem.child2));
                    return true;
                }
            }
            return false;
        });
        ruleIndexes.additionLeftRepacking = rules.size();
        rules.add((configuration) -> {
            final KThing firstKCellItem = configuration.getKLastElement();
            if (firstKCellItem.type == KThing.Type.AEXP_INT) {
                if (configuration.getKSize() > 1) {
                    KThing add = configuration.getKElementBeforeLast();
                    if (add.type == KThing.Type.KITEM_ADDITION_LEFT_MISSING) {
                        configuration.replaceKPrefix(2, Aexp.add(firstKCellItem, add.child1));
                        return true;
                    }
                }
            }
            return false;
        });
        ruleIndexes.additionRightUnpacking = rules.size();
        rules.add((configuration) -> {
            final KThing firstKCellItem = configuration.getKLastElement();
            if (firstKCellItem.type == KThing.Type.AEXP_ADD) {
                KThing first = firstKCellItem.child1;
                KThing second = firstKCellItem.child2;
                if (first.type == KThing.Type.AEXP_INT && !(second.type == KThing.Type.AEXP_INT)) {
                    configuration.replaceKPrefix(1, second, KItem.additionRightMissing(first));
                    return true;
                }
            }
            return false;
        });
        ruleIndexes.additionRightRepacking = rules.size();
        rules.add((configuration) -> {
            final KThing firstKCellItem = configuration.getKLastElement();
            if (firstKCellItem.type == KThing.Type.AEXP_INT) {
                if (configuration.getKSize() > 1) {
                    KThing add = configuration.getKElementBeforeLast();
                    if (add.type == KThing.Type.KITEM_ADDITION_RIGHT_MISSING) {
                        configuration.replaceKPrefix(2, Aexp.add(add.child1, firstKCellItem));
                        return true;
                    }
                }
            }
            return false;
        });

        ruleIndexes.lessOrEqualsLeftUnpacking = rules.size();
        rules.add((configuration) -> {
            final KThing firstKCellItem = configuration.getKLastElement();
            if (firstKCellItem.type == KThing.Type.BEXP_LESS_OR_EQUALS) {
                KThing first = firstKCellItem.child1;
                if (!(first.type == KThing.Type.AEXP_INT)) {
                    configuration.replaceKPrefix(1, first, KItem.lessOrEqualsLeftMissing(firstKCellItem.child2));
                    return true;
                }
            }
            return false;
        });
        ruleIndexes.lessOrEqualsLeftRepacking = rules.size();
        rules.add((configuration) -> {
            final KThing firstKCellItem = configuration.getKLastElement();
            if (firstKCellItem.type == KThing.Type.AEXP_INT) {
                if (configuration.getKSize() > 1) {
                    KThing less = configuration.getKElementBeforeLast();
                    if (less.type == KThing.Type.KITEM_LESS_OR_EQUALS_LEFT_MISSING) {
                        configuration.replaceKPrefix(2, Bexp.lessOrEquals(firstKCellItem, less.child1));
                        return true;
                    }
                }
            }
            return false;
        });
        ruleIndexes.lessOrEqualsRightUnpacking = rules.size();
        rules.add((configuration) -> {
            final KThing firstKCellItem = configuration.getKLastElement();
            if (firstKCellItem.type == KThing.Type.BEXP_LESS_OR_EQUALS) {
                KThing first = firstKCellItem.child1;
                KThing second = firstKCellItem.child1;
                if ((first.type == KThing.Type.AEXP_INT) && !(second.type == KThing.Type.AEXP_INT)) {
                    configuration.replaceKPrefix(1, second, KItem.lessOrEqualsRightMissing(first));
                    return true;
                }
            }
            return false;
        });
        ruleIndexes.lessOrEqualsRightRepacking = rules.size();
        rules.add((configuration) -> {
            final KThing firstKCellItem = configuration.getKLastElement();
            if (firstKCellItem.type == KThing.Type.AEXP_INT) {
                if (configuration.getKSize() > 1) {
                    KThing less = configuration.getKElementBeforeLast();
                    if (less.type == KThing.Type.KITEM_LESS_OR_EQUALS_RIGHT_MISSING) {
                        configuration.replaceKPrefix(2, Bexp.lessOrEquals(less.child1, firstKCellItem));
                        return true;
                    }
                }
            }
            return false;
        });

        ruleIndexes.notUnpacking = rules.size();
        rules.add((configuration) -> {
            final KThing firstKCellItem = configuration.getKLastElement();
            if (firstKCellItem.type == KThing.Type.BEXP_NOT) {
                KThing operand = firstKCellItem.child1;
                if (!(operand.type == KThing.Type.BEXP_BOOL)) {
                    configuration.replaceKPrefix(1, operand, KItem.notMissingOperand());
                    return true;
                }
            }
            return false;
        });
        ruleIndexes.notRepacking = rules.size();
        rules.add((configuration) -> {
            final KThing firstKCellItem = configuration.getKLastElement();
            if (firstKCellItem.type == KThing.Type.BEXP_BOOL) {
                if (configuration.getKSize() > 1) {
                    if (configuration.getKElementBeforeLast().type == KThing.Type.KITEM_NOT_MISSING_OPERAND) {
                        configuration.replaceKPrefix(2, Bexp.not(firstKCellItem));
                        return true;
                    }
                }
            }
            return false;
        });

        ruleIndexes.assignmentUnpacking = rules.size();
        rules.add((configuration) -> {
            final KThing firstKCellItem = configuration.getKLastElement();
            if (firstKCellItem.type == KThing.Type.STMT_ASSIGN) {
                KThing second = firstKCellItem.child2;
                if (!(second.type == KThing.Type.AEXP_INT)) {
                    configuration.replaceKPrefix(
                            1, second, KItem.assignmentMissingOperand(firstKCellItem.child1));
                    return true;
                }
            }
            return false;
        });
        ruleIndexes.assignmentRepacking = rules.size();
        rules.add((configuration) -> {
            final KThing firstKCellItem = configuration.getKLastElement();
            if (firstKCellItem.type == KThing.Type.AEXP_INT) {
                if (configuration.getKSize() > 1) {
                    KThing assignment = configuration.getKElementBeforeLast();
                    if (assignment.type == KThing.Type.KITEM_ASSIGNMENT_MISSING_OPERAND) {
                        configuration.replaceKPrefix(2, Stmt.assign(assignment.child1, firstKCellItem));
                        return true;
                    }
                }
            }
            return false;
        });

        ruleIndexes.ifUnpacking = rules.size();
        rules.add((configuration) -> {
            final KThing firstKCellItem = configuration.getKLastElement();
            if (firstKCellItem.type == KThing.Type.STMT_IF) {
                KThing condition = firstKCellItem.child1;
                if (!(condition.type == KThing.Type.BEXP_BOOL)) {
                    configuration.replaceKPrefix(
                                    1,
                                    condition,
                                    KItem.ifMissingCondition(firstKCellItem.child2, firstKCellItem.child3));
                    return true;
                }
            }
            return false;
        });
        ruleIndexes.ifRepacking = rules.size();
        rules.add((configuration) -> {
            final KThing firstKCellItem = configuration.getKLastElement();
            if (firstKCellItem.type == KThing.Type.BEXP_BOOL) {
                if (configuration.getKSize() > 1) {
                    KThing sif = configuration.getKElementBeforeLast();
                    if (sif.type == KThing.Type.KITEM_IF_MISSING_CONDITION) {
                        configuration.replaceKPrefix(
                                2, Stmt.sif(firstKCellItem, sif.child1, sif.child2));
                        return true;
                    }
                }
            }
            return false;
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
                        Stmt.assign(Id.of("n"), Aexp.aint(Int.of("100000000"))),
                        Stmt.sequence(
                                Stmt.assign(Id.of("sum"), Aexp.aint(Int.of("0"))),
                                Stmt.swhile(
                                        Bexp.not(Bexp.lessOrEquals(Aexp.id(Id.of("n")), Aexp.aint(Int.of("0")))),
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
