package com.runtimeverification.ktest.nonterminals;

import com.runtimeverification.ktest.KThing;

public class Ids implements KThing {
    public static Ids of(Id... ids) {
        Ids current = new Empty();
        for (int i = ids.length - 1; i >= 0; i--) {
            current = new NonEmpty(ids[i], current);
        }
        return current;
    }

    public static class Empty extends Ids {
        @Override
        public String toString() {
            return "Empty()";
        }
    }

    public static class NonEmpty extends Ids {
        private final Id id;
        private final Ids tail;

        private NonEmpty(Id id, Ids tail) {
            this.id = id;
            this.tail = tail;
        }

        public Ids getTail() {
            return tail;
        }

        public Id getId() {
            return id;
        }

        @Override
        public String toString() {
            return "NonEmpty(" + id + ", " + tail + ")";
        }
    }
}
