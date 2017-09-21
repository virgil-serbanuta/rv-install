package com.runtimeverification.ktest.rule.ruleterms;

import com.runtimeverification.ktest.rule.RuleTerm;

import java.util.Arrays;
import java.util.List;

public class Sequence implements RuleTerm {
    private final List<RuleTerm> terms;

    private Sequence(List<RuleTerm> terms) {
        this.terms = terms;
    }

    public static Sequence of(RuleTerm... terms) {
        return new Sequence(Arrays.asList(terms));
    }
}
