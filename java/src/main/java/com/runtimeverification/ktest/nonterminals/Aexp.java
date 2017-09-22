package com.runtimeverification.ktest.nonterminals;

import com.runtimeverification.ktest.KThing;

public abstract class Aexp implements KThing {

    public static Aexp aint(Int value) {
        return new IntAexp(value);
    }

    public static Aexp id(Id id) {
        return new IdAexp(id);
    }

    public static Aexp add(Aexp first, Aexp second) {
        return new AddAexp(first, second);
    }

    public static Aexp div(Aexp first, Aexp second) {
        return new DivAexp(first, second);
    }

    public static class AddAexp extends Aexp {
        private final Aexp first;
        private final Aexp second;

        private AddAexp(Aexp first, Aexp second) {
            this.first = first;
            this.second = second;
        }

        public Aexp getFirst() {
            return first;
        }

        public Aexp getSecond() {
            return second;
        }

        @Override
        public String toString() {
            return "AddAexp(" + first.toString() + ", " + second.toString() + ")";
        }
    }

    public static class DivAexp extends Aexp {
        private final Aexp first;
        private final Aexp second;

        private DivAexp(Aexp first, Aexp second) {
            this.first = first;
            this.second = second;
        }

        public Aexp getFirst() {
            return first;
        }

        public Aexp getSecond() {
            return second;
        }

        @Override
        public String toString() {
            return "DivAexp(" + first.toString() + ", " + second.toString() + ")";
        }
    }

    public static class IdAexp extends Aexp {
        private final Id id;

        private IdAexp(Id id) {
            this.id = id;
        }

        @Override
        public String toString() {
            return "IdAexp(" + id.toString() + ")";
        }

        public Id getId() {
            return id;
        }
    }

    public static class IntAexp extends Aexp {
        private final Int value;

        private IntAexp(Int value) {
            this.value = value;
        }

        public Int getInt() {
            return value;
        }

        @Override
        public String toString() {
            return "IntAexp(" + value.toString() + ")";
        }
    }
}
