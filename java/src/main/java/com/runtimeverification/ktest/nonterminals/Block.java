package com.runtimeverification.ktest.nonterminals;

import com.runtimeverification.ktest.KThing;

public abstract class Block implements KThing<Block> {
    public static Block stmt(KThingSource<Stmt> stmt) {
        return new StmtBlock(stmt);
    }

    @Override
    public Block get() {
        return this;
    }

    public static EmptyBlock empty() {
        return new EmptyBlock();
    }

    private static class EmptyBlock extends Block {
    }

    private static class StmtBlock extends Block {
        private final KThingSource<Stmt> stmt;

        private StmtBlock(KThingSource<Stmt> stmt) {
            this.stmt = stmt;
        }
    }
}
