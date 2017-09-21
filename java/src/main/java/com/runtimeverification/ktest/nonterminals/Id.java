package com.runtimeverification.ktest.nonterminals;

import com.runtimeverification.ktest.KThing;

public class Id implements KThing<Id> {
    private final String identifier;

    private Id(String identifier) {
        this.identifier = identifier;
    }

    public static Id of(String identifier) {
        return new Id(identifier);
    }

    @Override
    public Id get() {
        return this;
    }
}
