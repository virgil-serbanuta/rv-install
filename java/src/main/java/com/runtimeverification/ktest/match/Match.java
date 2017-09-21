package com.runtimeverification.ktest.match;

import com.runtimeverification.ktest.Context;
import com.runtimeverification.ktest.configuration.Cell;

import java.util.ArrayList;
import java.util.List;

public class Match {
    private final List<TermMatch> termMatches = new ArrayList<>();

    public boolean coversTheFullCell(Cell cell) {
        boolean hasStart = false;
        boolean hasEnd = false;
        for (TermMatch termMatch : termMatches) {
            hasStart = hasStart || (termMatch.getStart() == 0);
            hasEnd = hasEnd || (termMatch.getEnd() == cell.getTermCount());
        }
        return hasStart && hasEnd;
    }

    public void loadToContext(Context context) {
        for (TermMatch termMatch : termMatches) {
            if (!termMatch.loadToContext(context));
        }
    }
}
