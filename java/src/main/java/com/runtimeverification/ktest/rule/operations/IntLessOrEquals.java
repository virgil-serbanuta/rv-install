package com.runtimeverification.ktest.rule.operations;

import com.runtimeverification.ktest.nonterminals.Bool;
import com.runtimeverification.ktest.rule.ValueRuleTerm;

public class IntLessOrEquals implements Operation<Bool> {
    private final ValueRuleTerm first;
    private final ValueRuleTerm second;

    private IntLessOrEquals(ValueRuleTerm first, ValueRuleTerm second) {
        this.first = first;
        this.second = second;
    }

    public static IntLessOrEquals of(ValueRuleTerm first, ValueRuleTerm second) {
        return new IntLessOrEquals(first, second);
    }
}
