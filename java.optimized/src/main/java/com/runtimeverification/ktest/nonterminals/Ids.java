package com.runtimeverification.ktest.nonterminals;

import com.runtimeverification.ktest.KThing;

public class Ids{
    public static KThing of(KThing... ids) {
        KThing current = new KThing(KThing.Type.IDS_EMPTY);
        for (int i = ids.length - 1; i >= 0; i--) {
            current = new KThing(KThing.Type.IDS_NON_EMPTY, ids[i], current);
        }
        return current;
    }
}
