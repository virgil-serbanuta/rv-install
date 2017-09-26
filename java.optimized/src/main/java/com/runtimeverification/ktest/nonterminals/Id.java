package com.runtimeverification.ktest.nonterminals;

import com.runtimeverification.ktest.KThing;

public class Id extends KThing {
    private final String identifier;

    private Id(String identifier) {
        super(KThing.Type.ID);
        this.identifier = identifier;
    }

    public static Id of(String identifier) {
        return new Id(identifier);
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Id && identifier.equals(((Id) obj).identifier);
    }

    @Override
    public String toString() {
        return "ID(" + identifier + ")";
    }
}
