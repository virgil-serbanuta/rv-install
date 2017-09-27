package com.runtimeverification.ktest.nonterminals;

import com.runtimeverification.ktest.KThing;

public abstract class Aexp {
    public static KThing aint(KThing value) {
        return new KThing(KThing.Type.AEXP_INT, value);
    }

    public static KThing id(KThing id) {
        return new KThing(KThing.Type.AEXP_ID, id);
    }

    public static KThing add(KThing first, KThing second) {
        return new KThing(KThing.Type.AEXP_ADD, first, second);
    }

    public static KThing div(KThing first, KThing second) {
        return new KThing(KThing.Type.AEXP_DIV, first, second);
    }
}
