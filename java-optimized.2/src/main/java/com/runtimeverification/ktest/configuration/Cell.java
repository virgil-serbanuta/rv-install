package com.runtimeverification.ktest.configuration;

import com.runtimeverification.ktest.KThing;
import com.runtimeverification.ktest.tools.KMap;

import java.util.Arrays;

public class Cell {
    private final KThing[] kThings;

    private Cell(KThing[] kThings) {
        this.kThings = kThings;
    }

    public static Cell of(KThing kThing) {
        return new Cell(new KThing[] {kThing});
    }

    public int getTermCount() {
        return kThings.length;
    }

    public KThing getTerm(int index) {
        return kThings[index];
    }

    public Cell replacePrefix(int count, KThing... values) {
        KThing[] newKThings = new KThing[kThings.length - count + values.length];
        for (int i = 0; i < values.length; i++) {
            newKThings[i] = values[i];
        }
        for (int i = count; i < kThings.length; i++) {
            newKThings[i - count + values.length] = kThings[i];
        }
        return new Cell(newKThings);
    }

    public Cell removePrefix(int count) {
        KThing[] newKThings = new KThing[kThings.length - count];
        for (int i = count; i < kThings.length; i++) {
            newKThings[i - count] = kThings[i];
        }
        return new Cell(newKThings);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "(" + Arrays.toString(kThings) + ")";
    }
}
