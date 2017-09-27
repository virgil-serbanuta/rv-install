package com.runtimeverification.ktest;

import java.util.Arrays;
import java.util.Map;
import java.util.Stack;

class Configuration {
    private KThing[] k;
    private final Map<KThing, KThing> state;

    private int kSize = 0;

    public Configuration(KThing k, Map<KThing, KThing> state) {
        this.k = new KThing[10];
        this.k[0] = k;
        this.kSize = 1;
        this.state = state;
    }

    public void replaceKPrefix(int count) {
        kSize -=count;
    }

    public void replaceKPrefix(int count, KThing value1) {
        kSize -=count;
        if (kSize == k.length) {
            k = Arrays.copyOf(k, 2 * k.length);
        }
        k[kSize++] = value1;
    }

    public void replaceKPrefix(int count, KThing value1, KThing value2) {
        kSize -=count;
        if (kSize + 1 > k.length) {
            k = Arrays.copyOf(k, 2 * k.length);
        }
        k[kSize++] = value2;
        k[kSize++] = value1;
    }

    public int getKSize() {
        return kSize;
    }
    public KThing getKLastElement() {
        return k[kSize - 1];
    }
    public KThing getKElementBeforeLast() {
        return k[kSize - 2];
    }

    public Map<KThing, KThing> getState() {
        return state;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "(\n  " + Arrays.toString(Arrays.copyOfRange(k, 0, kSize)) + ",\n  " + state + ")";
    }
}

