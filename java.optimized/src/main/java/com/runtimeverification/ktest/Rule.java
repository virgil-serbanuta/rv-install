package com.runtimeverification.ktest;

import java.util.Map;
import java.util.Optional;

@FunctionalInterface
public interface Rule {
    boolean apply(Configuration configuration);
}
