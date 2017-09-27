package com.runtimeverification.ktest;

import com.runtimeverification.ktest.tools.KMap;

import java.util.Optional;

@FunctionalInterface
public interface Rule {
    Optional<Configuration> apply(
            Configuration configuration, KThing firstCellItem, Optional<KThing> secondKCellItem, KMap stateCellMap);
}
