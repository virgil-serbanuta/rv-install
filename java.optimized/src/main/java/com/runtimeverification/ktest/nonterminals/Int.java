package com.runtimeverification.ktest.nonterminals;

import com.runtimeverification.ktest.KThing;

public class Int extends KThing {
    private final int value;

    public static Int of(int value) {
        return new Int(value);
    }

    public static Int of(String value) {
        return new Int(Integer.valueOf(value));
    }

    private Int(int value) {
        super(KThing.Type.INT);
        this.value = value;
    }

    @Override
    public int getAsInt() {
        return value;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Int && (value == ((Int) obj).value);
    }

    @Override
    public String toString() {
        return "INT(" + value + ")";
    }
}
