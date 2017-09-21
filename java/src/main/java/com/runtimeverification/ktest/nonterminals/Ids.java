package com.runtimeverification.ktest.nonterminals;

import com.runtimeverification.ktest.KThing;

public class Ids implements KThing<Ids> {
    private final Id[] ids;

    private Ids(Id[] ids) {
        this.ids = ids;
    }

    public static Ids of(Id... ids) {
        return new Ids(ids);
    }

    @Override
    public Ids get() {
        return this;
    }
}
