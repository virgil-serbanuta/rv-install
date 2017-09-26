package com.runtimeverification.ktest.nonterminals;

import com.runtimeverification.ktest.KThing;

public class Id implements KThing {
    private final String identifier;

    private Id(String identifier) {
        this.identifier = identifier;
    }

    public static Id of(String identifier) {
        return new Id(identifier);
    }

    @Override
    public String toString() {
        return "Id(" + identifier + ")";
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Id && identifier.equals(((Id) obj).identifier);
    }
}
