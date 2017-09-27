package com.runtimeverification.ktest.tools;

import com.runtimeverification.ktest.KThing;

import java.util.HashMap;
import java.util.Map;

public class KMap implements KThing {
    private final Map<KThing, KThing> map;

    public KMap() {
        this.map = new HashMap<>();
    }

    public KMap(Map<KThing, KThing> map) {
        this.map = map;
    }

    public Map<KThing, KThing> getMap() {
        return map;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "(" + map.toString() + ")";
    }
}
