package com.runtimeverification.ktest.nonterminals;

import com.runtimeverification.ktest.KThing;

public abstract class Block implements KThing {
    public static Block stmt(Stmt stmt) {
        return new StmtBlock(stmt);
    }

    public static EmptyBlock empty() {
        return new EmptyBlock();
    }

    public static class EmptyBlock extends Block {
        @Override
        public String toString() {
            return "EmptyBlock()";
        }
    }

    public static class StmtBlock extends Block {
        private final Stmt stmt;

        private StmtBlock(Stmt stmt) {
            this.stmt = stmt;
        }

        public Stmt getStmt() {
            return stmt;
        }

        @Override
        public String toString() {
            return "StmtBlock(" + stmt.toString() + ")";
        }
    }
}
