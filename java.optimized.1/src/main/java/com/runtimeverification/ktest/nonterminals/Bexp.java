package com.runtimeverification.ktest.nonterminals;

import com.runtimeverification.ktest.KThing;

public abstract class Bexp implements KThing {
    public static Bexp less(Aexp first, Aexp second) {
        return new LessBexp(first, second);
    }

    public static Bexp and(Bexp first, Bexp second) {
        return new AndBexp(first, second);
    }

    public static Bexp not(Bexp operand) {
        return new NotBexp(operand);
    }

    public static Bexp bool(Bool value) {
        return new BoolBexp(value);
    }

    public static class AndBexp extends Bexp {
        private final Bexp first;
        private final Bexp second;

        private AndBexp(Bexp first, Bexp second) {
            this.first = first;
            this.second = second;
        }

        public Bexp getFirst() {
            return first;
        }

        public Bexp getSecond() {
            return second;
        }

        @Override
        public String toString() {
            return "AndBexp(" + first.toString() + ", " + second.toString() + ")";
        }
    }

    public static class NotBexp extends Bexp {
        private final Bexp operand;

        private NotBexp(Bexp operand) {
            this.operand = operand;
        }

        public Bexp getOperand() {
            return operand;
        }

        @Override
        public String toString() {
            return "NotBexp(" + operand.toString() + ")";
        }
    }

    public static class LessBexp extends Bexp {
        private final Aexp first;
        private final Aexp second;

        private LessBexp(Aexp first, Aexp second) {
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
            return "LessBexp(" + first.toString() + ", " + second.toString() + ")";
        }
    }

    public static class BoolBexp extends Bexp {
        private final Bool value;

        public BoolBexp(Bool value) {
            this.value = value;
        }

        public Bool getBool() {
            return value;
        }

        @Override
        public String toString() {
            return "BoolBexp(" + value.toString() + ")";
        }
    }
}
