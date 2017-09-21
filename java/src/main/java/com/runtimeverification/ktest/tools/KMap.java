package com.runtimeverification.ktest.tools;

import com.runtimeverification.ktest.KThing;

import java.util.HashMap;
import java.util.Map;

public class KMap implements KThing {
    Map<KThing, KThing> map = new HashMap<>();

    public Map<KThing, KThing> getMap() {
        return map;
    }
}
