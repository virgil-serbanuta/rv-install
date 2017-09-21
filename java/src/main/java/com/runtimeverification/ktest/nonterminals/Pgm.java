package com.runtimeverification.ktest.nonterminals;

import com.runtimeverification.ktest.KThing;

public class Pgm implements KThing<Pgm> {
    private final Ids ids;
    private final Stmt stmt;

    private Pgm(Ids ids, Stmt stmt) {
        this.ids = ids;
        this.stmt = stmt;
    }

    public static Pgm of(Ids ids, Stmt stmt) {
        return new Pgm(ids, stmt);
    }

    @Override
    public Pgm get() {
        return this;
    }
}
