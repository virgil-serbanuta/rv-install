package com.runtimeverification.ktest.nonterminals;

import com.runtimeverification.ktest.KThing;

public class Stmt {
    public static KThing block(KThing block) {
        return new KThing(KThing.Type.STMT_BLOCK, block);
    }

    public static KThing assign(KThing id, KThing aexp) {
        return new KThing(KThing.Type.STMT_ASSIGN, id, aexp);
    }

    public static KThing sequence(KThing first, KThing second) {
        return new KThing(KThing.Type.STMT_SEQUENCE, first, second);
    }

    public static KThing swhile(KThing condition, KThing block) {
        return new KThing(KThing.Type.STMT_WHILE, condition, block);
    }

    public static KThing sif(KThing condition, KThing ithen, KThing ielse) {
        return new KThing(KThing.Type.STMT_IF, condition, ithen, ielse);
    }
}
