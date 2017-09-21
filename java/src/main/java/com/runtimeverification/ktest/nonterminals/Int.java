package com.runtimeverification.ktest.nonterminals;

import com.runtimeverification.ktest.KThing;
import com.runtimeverification.ktest.rule.ValueRuleTerm;

public class Int implements KThing<Int> {
    private final int value;

    private Int(int value) {
        this.value = value;
    }

    public static Int of(String value) {
        return new Int(Integer.valueOf(value));
    }

    public int getValue() {
        return value;
    }

    public static Int of(int value) {
        return new Int(value);
    }
}
