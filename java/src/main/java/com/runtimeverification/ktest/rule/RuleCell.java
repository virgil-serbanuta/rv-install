package com.runtimeverification.ktest.rule;

import com.runtimeverification.ktest.Context;
import com.runtimeverification.ktest.configuration.Cell;
import com.runtimeverification.ktest.match.Match;
import com.runtimeverification.ktest.rule.ruleterms.Sequence;

public class RuleCell {
    private final String name;
    private final RuleTerm term;

    private RuleCell(String name, RuleTerm term) {
        this.name = name;
        this.term = term;
    }

    public static RuleCell of(String name, RuleTerm... terms) {
        return new RuleCell(name, Sequence.of(terms));
    }

    public String getName() {
        return name;
    }

    public boolean matchCell(Cell cell, Context context) {
        Match match = new Match();
        while (term.nextMatch(0, 0, cell, context, match)) {
            if (match.coversTheFullCell(cell)) {
                match.loadToContext(context);
                return true;
            }
        }
        return false;
    }
}
