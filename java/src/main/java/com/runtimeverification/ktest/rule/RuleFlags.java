package com.runtimeverification.ktest.rule;

import java.util.Arrays;
import java.util.List;

public class RuleFlags {
    private final List<Type> types;

    private RuleFlags(List<Type> types) {
        this.types = types;
    }

    public static RuleFlags of(Type... types) {
        return new RuleFlags(Arrays.asList(types));
    }

    public enum Type {STRUCTURAL}
}
