package com.runtimeverification.ktest.rule;

import com.runtimeverification.ktest.Context;
import com.runtimeverification.ktest.KThing;
import com.runtimeverification.ktest.configuration.Cell;
import com.runtimeverification.ktest.match.Match;

public interface RuleTerm {
    boolean nextMatch(int cellIndex, int everythingMatchesIndex, Cell cell, Context context, Match match);
}
