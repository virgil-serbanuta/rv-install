package com.runtimeverification.ktest.nonterminals;

import com.runtimeverification.ktest.KThing;
import com.runtimeverification.ktest.rule.RuleTerm;

public class Bool implements KThing<Bool> {
    private final boolean value;

    private Bool(boolean value) {
        this.value = value;
    }

    public static Bool of(String value) {
        return new Bool(Boolean.valueOf(value));
    }

    public static Bool of(boolean b) {
        return new Bool(b);
    }

    public boolean getValue() {
        return value;
    }
}
