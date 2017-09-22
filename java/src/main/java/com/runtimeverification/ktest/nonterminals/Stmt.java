package com.runtimeverification.ktest.nonterminals;

import com.runtimeverification.ktest.KThing;

public abstract class Stmt implements KThing {
    public static Stmt block(Block block) {
        return new BlockStmt(block);
    }

    public static Stmt assign(Id id, Aexp aexp) {
        return new AssignStmt(id, aexp);
    }

    public static Stmt sequence(Stmt first, Stmt second) {
        return new SequenceStmt(first, second);
    }

    public static Stmt swhile(Bexp condition, Block block) {
        return new WhileStmt(condition, block);
    }

    public static Stmt sif(Bexp condition, Block ithen, Block ielse) {
        return new IfStmt(condition, ithen, ielse);
    }

    public static class WhileStmt extends Stmt {
        private final Bexp condition;
        private final Block block;

        private WhileStmt(Bexp condition, Block block) {
            this.condition = condition;
            this.block = block;
        }

        public Bexp getCondition() {
            return condition;
        }

        public Block getBlock() {
            return block;
        }

        @Override
        public String toString() {
            return this.getClass().getName() + "(" + condition + ", " + block + ")";
        }
    }

    public static class IfStmt extends Stmt {
        private final Bexp condition;
        private final Block ithen;
        private final Block ielse;

        private IfStmt(Bexp condition, Block ithen, Block ielse) {
            this.condition = condition;
            this.ithen = ithen;
            this.ielse = ielse;
        }

        public Bexp getCondition() {
            return condition;
        }

        public Block getIthen() {
            return ithen;
        }

        public Block getIelse() {
            return ielse;
        }

        @Override
        public String toString() {
            return this.getClass().getName() + "(" + condition + ", " + ithen + ", " + ielse + ")";
        }
    }

    public static class SequenceStmt extends Stmt {
        private final Stmt first;
        private final Stmt second;

        private SequenceStmt(Stmt first, Stmt second) {
            this.first = first;
            this.second = second;
        }

        public Stmt getFirst() {
            return first;
        }

        public Stmt getSecond() {
            return second;
        }

        @Override
        public String toString() {
            return this.getClass().getName() + "(" + first + ", " + second + ")";
        }
    }

    public static class AssignStmt extends Stmt {
        private final Id id;
        private final Aexp aexp;

        private AssignStmt(Id id, Aexp aexp) {
            this.id = id;
            this.aexp = aexp;
        }

        public Aexp getAexp() {
            return aexp;
        }

        public Id getId() {
            return id;
        }

        @Override
        public String toString() {
            return this.getClass().getSimpleName() + "(" + id + ", " + aexp + ")";
        }
    }

    public static class BlockStmt extends Stmt {
        private final Block block;

        private BlockStmt(Block block) {
            this.block = block;
        }

        public Block getBlock() {
            return block;
        }

        @Override
        public String toString() {
            return this.getClass().getSimpleName() + "(" + block + ")";
        }
    }
}
