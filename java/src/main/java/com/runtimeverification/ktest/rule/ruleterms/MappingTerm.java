package com.runtimeverification.ktest.rule.ruleterms;

import com.runtimeverification.ktest.rule.RuleTerm;

public class MappingTerm implements RuleTerm {
    private final KThingSource key;
    private final RuleTerm value;

    private MappingTerm(KThingSource key, RuleTerm value) {
        this.key = key;
        this.value = value;
    }

    public static MappingTerm of(KThingSource key, RuleTerm value) {
        return new MappingTerm(key, value);
    }
}
