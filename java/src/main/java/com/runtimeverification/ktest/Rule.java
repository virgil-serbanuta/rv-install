package com.runtimeverification.ktest;

import com.runtimeverification.ktest.configuration.Cell;
import com.runtimeverification.ktest.rule.RuleCell;
import com.runtimeverification.ktest.rule.RuleFlags;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Rule {
    private final Optional<RuleCondition> condition;
    private final RuleFlags ruleFlags;
    private final Map<String, RuleCell> cells;

    private Rule(Optional<RuleCondition> condition, RuleFlags ruleFlags, List<RuleCell> cells) {
        this.condition = condition;
        this.ruleFlags = ruleFlags;
        this.cells = new HashMap<>();
        for (RuleCell cell : cells) {
            this.cells.put(cell.getName(), cell);
        }
    }

    public static Rule of(RuleFlags flags, RuleCell... cells) {
        return new Rule(Optional.empty(), flags, Arrays.asList(cells));
    }

    public static Rule of(RuleCell... cells) {
        return new Rule(Optional.empty(), RuleFlags.of(), Arrays.asList(cells));
    }

    public static Rule of(RuleCondition condition, RuleCell... cells) {
        return new Rule(Optional.of(condition), RuleFlags.of(), Arrays.asList(cells));
    }

    public boolean matchCell(Cell cell, Context context) {
        RuleCell ruleCell = cells.get(cell.getName());
        if (ruleCell == null) {
            return true;
        }
        return ruleCell.matchCell(cell, context);
    }
}
