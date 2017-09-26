package com.runtimeverification.ktest.nonterminals;

import com.runtimeverification.ktest.KThing;

public class Block {
    public static KThing stmt(KThing stmt) {
        return new KThing(KThing.Type.BLOCK_STMT, stmt);
    }

    public static KThing empty() {
        return new KThing(KThing.Type.BLOCK_EMPTY);
    }
}
