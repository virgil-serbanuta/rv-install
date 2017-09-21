package com.runtimeverification.ktest.nonterminals;

import com.runtimeverification.ktest.KThing;

public abstract class Stmt implements KThing<Stmt> {
    public static Stmt block(KThingSource<Block> block) {
        return new BlockStmt(block);
    }

    public static Stmt assign(Id id, KThingSource<Aexp> aexp) {
        return new AssignStmt(id, aexp);
    }

    public static Stmt sequence(KThingSource<Stmt> first, KThingSource<Stmt> second) {
        return new SequenceStmt(first, second);
    }

    public static Stmt swhile(KThingSource<Bexp> condition, KThingSource<Block> block) {
        return new WhileStmt(condition, block);
    }

    public static Stmt sif(KThingSource<Bexp> condition, KThingSource<Block> ithen, KThingSource<Block> ielse) {
        return new IfStmt(condition, ithen, ielse);
    }

    @Override
    public Stmt get() {
        return this;
    }

    private static class WhileStmt extends Stmt {
        private final KThingSource<Bexp> condition;
        private final KThingSource<Block> block;

        private WhileStmt(KThingSource<Bexp> condition, KThingSource<Block> block) {
            this.condition = condition;
            this.block = block;
        }
    }

    private static class IfStmt extends Stmt {
        private final KThingSource<Bexp> condition;
        private final KThingSource<Block> ithen;
        private final KThingSource<Block> ielse;

        private IfStmt(KThingSource<Bexp> condition, KThingSource<Block> ithen, KThingSource<Block> ielse) {
            this.condition = condition;
            this.ithen = ithen;
            this.ielse = ielse;
        }
    }

    private static class SequenceStmt extends Stmt {
        private final KThingSource<Stmt> first;
        private final KThingSource<Stmt> second;

        private SequenceStmt(KThingSource<Stmt> first, KThingSource<Stmt> second) {
            this.first = first;
            this.second = second;
        }
    }

    private static class AssignStmt extends Stmt {
        private final Id id;
        private final KThingSource<Aexp> aexp;

        private AssignStmt(Id id, KThingSource<Aexp> aexp) {
            this.id = id;
            this.aexp = aexp;
        }
    }

    private static class BlockStmt extends Stmt {
        private final KThingSource<Block> block;

        private BlockStmt(KThingSource<Block> block) {
            this.block = block;
        }
    }
}
