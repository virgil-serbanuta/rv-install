package com.runtimeverification.ktest.rule.ruleterms;

import com.runtimeverification.ktest.rule.RuleTerm;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Transform implements RuleTerm {
    private final List<RuleTerm> existing;
    private final List<KThingSource> result;

    private Transform(List<RuleTerm> existing, List<KThingSource> result) {
        this.existing = existing;
        this.result = result;
    }

    public static Transform of(RuleTerm existing, KThingSource... result) {
        return new Transform(Collections.singletonList(existing), Arrays.asList(result));
    }

    public static Transform of(List<RuleTerm> existing, KThingSource... result) {
        return new Transform(existing, Arrays.asList(result));
    }
}
