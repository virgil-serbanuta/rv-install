package com.runtimeverification.ktest.configuration;

import com.runtimeverification.ktest.KThing;
import com.runtimeverification.ktest.tools.KMap;

import java.util.Arrays;

public class Cell {
    private final String name;
    private final KThing[] kThings;
    private final Attribute[] attributes;

    private Cell(String name, KThing[] kThings, Attribute[] attributes) {
        this.name = name;
        this.kThings = kThings;
        this.attributes = attributes;
    }

    public static Cell of(String name, KThing kThing, Attribute... attributes) {
        return new Cell(name, new KThing[] {kThing}, attributes);
    }

    public String getName() {
        return name;
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
        return new Cell(name, newKThings, attributes);
    }

    public Cell removePrefix(int count) {
        KThing[] newKThings = new KThing[kThings.length - count];
        for (int i = count; i < kThings.length; i++) {
            newKThings[i - count] = kThings[i];
        }
        return new Cell(name, newKThings, attributes);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "(" + name + ", " + Arrays.toString(kThings) + ")";
    }
}
