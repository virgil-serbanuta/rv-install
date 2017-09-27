package com.runtimeverification.ktest.nonterminals;

import com.runtimeverification.ktest.KThing;

public class Bool extends KThing {
    private final boolean value;

    private Bool(boolean value) {
        super(KThing.Type.BOOL);
        this.value = value;
    }

    public static Bool of(boolean b) {
        return new Bool(b);
    }

    @Override
    public boolean getAsBool() {
        return value;
    }

    @Override
    public int hashCode() {
        return Boolean.hashCode(value);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Bool && (value == ((Bool) obj).value);
    }

    @Override
    public String toString() {
        return "BOOL(" + value + ")";
    }
}
