package com.runtimeverification.ktest.optimizations;

public class Pair<S, T> {
    private final S first;
    private final T second;

    public Pair(S first, T second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public String toString() {
        return "Pair<" + first.toString() + "," + second.toString() + ">";
    }

    @Override
    public int hashCode() {
        return first.hashCode() ^ second.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Pair)) {
            return false;
        }
        Pair p = (Pair) obj;
        return first.equals(p.first) && second.equals(p.second);
    }

    public T getSecond() {
        return second;
    }

    public S getFirst() {
        return first;
    }
}
