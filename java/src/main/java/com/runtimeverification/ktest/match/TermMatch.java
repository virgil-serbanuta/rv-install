package com.runtimeverification.ktest.match;

import com.runtimeverification.ktest.Context;
import com.runtimeverification.ktest.KThing;
import com.runtimeverification.ktest.configuration.Cell;
import com.runtimeverification.ktest.rule.RuleTerm;
import com.runtimeverification.ktest.rule.ruleterms.Anything;

import java.util.HashMap;
import java.util.Map;

public class TermMatch {
    private final RuleTerm term;
    private final int start;
    private final int end;
    private final Map<String, KThing> variableNameToMatch;

    public TermMatch(RuleTerm term, int start, int end) {
        this.term = term;
        this.start = start;
        this.end = end;
        this.variableNameToMatch = new HashMap<>();
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public boolean loadToContext(Cell cell, Context context) {
        if (end - start > 1) {
            assert term instanceof Anything;
        }

    }
}
