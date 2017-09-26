package com.runtimeverification.ktest.nonterminals;

import com.runtimeverification.ktest.KThing;

public class Pgm implements KThing {
    private final Ids ids;
    private final Stmt stmt;

    private Pgm(Ids ids, Stmt stmt) {
        this.ids = ids;
        this.stmt = stmt;
    }

    public static Pgm of(Ids ids, Stmt stmt) {
        return new Pgm(ids, stmt);
    }

    public Ids getIds() {
        return ids;
    }

    public Stmt getStmt() {
        return stmt;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "(" + ids + ", " + stmt + ")";
    }
}
