package com.runtimeverification.ktest.nonterminals;

import com.runtimeverification.ktest.KThing;

public class Pgm {
    public static KThing of(KThing ids, KThing stmt) {
        return new KThing(KThing.Type.PGM, ids, stmt);
    }
}
