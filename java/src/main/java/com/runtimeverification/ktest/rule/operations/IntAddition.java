package com.runtimeverification.ktest.rule.operations;

import com.runtimeverification.ktest.nonterminals.Int;
import com.runtimeverification.ktest.rule.ValueRuleTerm;

public class IntAddition implements Operation<Int> {
    private final ValueRuleTerm first;
    private final ValueRuleTerm second;

    private IntAddition(ValueRuleTerm first, ValueRuleTerm second) {
        this.first = first;
        this.second = second;
    }

    public static IntAddition of(ValueRuleTerm first, ValueRuleTerm second) {
        return new IntAddition(first, second);
    }
}
