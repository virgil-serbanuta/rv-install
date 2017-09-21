package com.runtimeverification.ktest.rule.operations;

import com.runtimeverification.ktest.nonterminals.Bool;

public class BoolAnd implements Operation<Bool> {
    private final KThingSource<Bool> left;
    private final KThingSource<Bool> right;

    private BoolAnd(KThingSource<Bool> left, KThingSource<Bool> right) {
        this.left = left;
        this.right = right;
    }

    public static BoolAnd of(KThingSource<Bool> left, KThingSource<Bool> right) {
        return new BoolAnd(left, right);
    }
}