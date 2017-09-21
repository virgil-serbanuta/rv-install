package com.runtimeverification.ktest.rule.operations;

import com.runtimeverification.ktest.nonterminals.Bool;

public class BoolNot implements Operation<Bool> {
    private final KThingSource<Bool> thing;

    private BoolNot(KThingSource<Bool> thing) {
        this.thing = thing;
    }

    public static BoolNot of(KThingSource<Bool> thing) {
        return new BoolNot(thing);
    }
}
