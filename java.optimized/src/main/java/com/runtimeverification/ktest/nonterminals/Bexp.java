package com.runtimeverification.ktest.nonterminals;

import com.runtimeverification.ktest.KThing;

public abstract class Bexp {
    public static KThing lessOrEquals(KThing first, KThing second) {
        return new KThing(KThing.Type.BEXP_LESS_OR_EQUALS, first, second);
    }

    public static KThing and(KThing first, KThing second) {
        return new KThing(KThing.Type.BEXP_AND, first, second);
    }

    public static KThing not(KThing operand) {
        return new KThing(KThing.Type.BEXP_NOT, operand);
    }

    public static KThing bool(KThing value) {
        return new KThing(KThing.Type.BEXP_BOOL, value);
    }
}
