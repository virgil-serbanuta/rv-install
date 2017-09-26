package com.runtimeverification.ktest;

import com.runtimeverification.ktest.configuration.Cell;
import com.runtimeverification.ktest.tools.KMap;

import java.util.Optional;

class Configuration {
    private final Cell k;
    private final Cell state;

    public Configuration() {
        this.k = Cell.of(new KMap());
        this.state = Cell.of(new KMap());
    }

    private Configuration(Cell k, Cell state) {
        this.k = k;
        this.state = state;
    }

    public Configuration replaceKCell(Cell cell) {
        return new Configuration(cell, state);
    }

    public Configuration replaceStateCell(Cell cell) {
        return new Configuration(k, cell);
    }

    public Cell getK() {
        return k;
    }

    public Cell getState() {
        return state;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "(\n  " + k + ",\n  " + state + ")";
    }
}

