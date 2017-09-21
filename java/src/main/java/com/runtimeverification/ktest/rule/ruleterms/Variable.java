package com.runtimeverification.ktest.rule.ruleterms;

import com.runtimeverification.ktest.KThing;
import com.runtimeverification.ktest.rule.ValueRuleTerm;

import java.util.Optional;

public class Variable implements ValueRuleTerm {
    private final Optional<String> name;
    private final Class<? extends KThing> type;

    private Variable(Optional<String> name, Class<? extends KThing> type) {
        this.name = name;
        this.type = type;
    }

    public static Variable of(String name, Class<? extends KThing> type) {
        return new Variable(Optional.of(name), type);
    }

    public static Variable of(String name) {
        return of(name, KThing.class);
    }

    public static Variable ignored() {
        return new Variable(Optional.empty(), KThing.class);
    }
}
