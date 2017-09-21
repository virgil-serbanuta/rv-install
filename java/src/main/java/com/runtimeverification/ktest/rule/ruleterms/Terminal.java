package com.runtimeverification.ktest.rule.ruleterms;

import com.runtimeverification.ktest.rule.RuleTerm;

public class Terminal implements RuleTerm {
    private final String text;

    private Terminal(String text) {
        this.text = text;
    }

    public static Terminal of(String text) {
        return new Terminal(text);
    }
}
