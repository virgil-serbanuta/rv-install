package com.runtimeverification.ktest.configuration;

import com.runtimeverification.ktest.KThing;
import com.runtimeverification.ktest.tools.KMap;

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

    public Cell replacePrefix(int count, KThing value) {
        KThing[] newKThings = new KThing[kThings.length - count + 1];
        newKThings[0] = value;
        for (int i = count; i < kThings.length; i++) {
            newKThings[i - count + 1] = kThings[i];
        }
        return new Cell(name, newKThings, attributes);
    }
}
