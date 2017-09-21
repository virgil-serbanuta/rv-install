package com.runtimeverification.ktest.rule.ruleterms;

import com.runtimeverification.ktest.rule.RuleTerm;

public class Anything implements RuleTerm {
    public static Anything of() {
        return new Anything();
    }
}
