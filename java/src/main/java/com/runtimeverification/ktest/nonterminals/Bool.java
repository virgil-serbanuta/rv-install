package com.runtimeverification.ktest.nonterminals;

import com.runtimeverification.ktest.KThing;

public class Bool implements KThing {
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

    @Override
    public String toString() {
        return "Bool(" + value + ")";
    }
}
