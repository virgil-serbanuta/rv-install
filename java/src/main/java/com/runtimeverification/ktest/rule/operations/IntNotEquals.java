package com.runtimeverification.ktest.rule.operations;

import com.runtimeverification.ktest.nonterminals.Bool;
import com.runtimeverification.ktest.nonterminals.Int;

public class IntNotEquals implements Operation<Bool> {
    private final KThingSource<Int> first;
    private final KThingSource<Int> second;

    private IntNotEquals(KThingSource<Int> first, KThingSource<Int> second) {
        this.first = first;
        this.second = second;
    }

    public static IntNotEquals of(KThingSource<Int> first, KThingSource<Int> second) {
        return new IntNotEquals(first, second);
    }
}
