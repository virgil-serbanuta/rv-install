package com.runtimeverification.ktest.rule.operations;

import com.runtimeverification.ktest.nonterminals.Int;
import com.runtimeverification.ktest.rule.ValueRuleTerm;

public class IntDivision implements Operation<Int> {
    private final ValueRuleTerm first;
    private final ValueRuleTerm second;

    private IntDivision(ValueRuleTerm first, ValueRuleTerm second) {
        this.first = first;
        this.second = second;
    }

    public static IntDivision of(ValueRuleTerm first, ValueRuleTerm second) {
        return new IntDivision(first, second);
    }
}
