package com.runtimeverification.ktest;

import com.runtimeverification.ktest.nonterminals.Bool;
import com.runtimeverification.ktest.rule.operations.Operation;

public class RuleCondition {
    private final Operation<Bool> operation;

    private RuleCondition(Operation<Bool> operation) {
        this.operation = operation;
    }

    public static RuleCondition of(Operation<Bool> operation) {
        return new RuleCondition(operation);
    }
}
