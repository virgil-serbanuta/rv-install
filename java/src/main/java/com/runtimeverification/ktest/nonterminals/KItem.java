package com.runtimeverification.ktest.nonterminals;

import com.runtimeverification.ktest.KThing;

public class KItem implements KThing {
    public static DivisionLeftMissing divisionLeftMissing(Aexp right) {
        return new DivisionLeftMissing(right);
    }
    public static DivisionRightMissing divisionRightMissing(Aexp left) {
        return new DivisionRightMissing(left);
    }

    public static AdditionLeftMissing additionLeftMissing(Aexp right) {
        return new AdditionLeftMissing(right);
    }
    public static AdditionRightMissing additionRightMissing(Aexp left) {
        return new AdditionRightMissing(left);
    }

    public static LessOrEqualsLeftMissing lessOrEqualsLeftMissing(Aexp right) {
        return new LessOrEqualsLeftMissing(right);
    }
    public static LessOrEqualsRightMissing lessOrEqualsRightMissing(Aexp left) {
        return new LessOrEqualsRightMissing(left);
    }

    public static NotMissingOperand notMissingOperand() {
        return new NotMissingOperand();
    }

    public static AndLeftMissing andLeftMissing(Bexp right) {
        return new AndLeftMissing(right);
    }

    public static AssignmentMissingOperand assignmentMissingOperand(Id left) {
        return new AssignmentMissingOperand(left);
    }

    public static IfMissingCondition ifMissingCondition(Block ithen, Block ielse) {
        return new IfMissingCondition(ithen, ielse);
    }

    public static class DivisionLeftMissing extends KItem {
        private final Aexp right;

        private DivisionLeftMissing(Aexp right) {
            this.right = right;
        }

        public Aexp getRight() {
            return right;
        }

        @Override
        public String toString() {
            return this.getClass().getSimpleName() + "(" + right + ")";
        }
    }

    public static class DivisionRightMissing extends KItem {
        private final Aexp left;

        private DivisionRightMissing(Aexp left) {
            this.left = left;
        }

        public Aexp getLeft() {
            return left;
        }

        @Override
        public String toString() {
            return this.getClass().getSimpleName() + "(" + left + ")";
        }
    }

    public static class AdditionLeftMissing extends KItem {
        private final Aexp right;

        private AdditionLeftMissing(Aexp right) {
            this.right = right;
        }

        public Aexp getRight() {
            return right;
        }

        @Override
        public String toString() {
            return this.getClass().getSimpleName() + "(" + right + ")";
        }
    }

    public static class AdditionRightMissing extends KItem {
        private final Aexp left;

        private AdditionRightMissing(Aexp left) {
            this.left = left;
        }

        public Aexp getLeft() {
            return left;
        }

        @Override
        public String toString() {
            return this.getClass().getSimpleName() + "(" + left + ")";
        }
    }

    public static class LessOrEqualsLeftMissing extends KItem {
        private final Aexp right;

        private LessOrEqualsLeftMissing(Aexp right) {
            this.right = right;
        }

        public Aexp getRight() {
            return right;
        }

        @Override
        public String toString() {
            return this.getClass().getSimpleName() + "(" + right + ")";
        }
    }

    public static class LessOrEqualsRightMissing extends KItem {
        private final Aexp left;

        private LessOrEqualsRightMissing(Aexp left) {
            this.left = left;
        }

        public Aexp getLeft() {
            return left;
        }

        @Override
        public String toString() {
            return this.getClass().getSimpleName() + "(" + left + ")";
        }
    }

    public static class NotMissingOperand extends KItem {
        private NotMissingOperand() {
        }

        @Override
        public String toString() {
            return this.getClass().getSimpleName() + "()";
        }
    }

    public static class AndLeftMissing extends KItem {
        private final Bexp right;

        private AndLeftMissing(Bexp right) {
            this.right = right;
        }

        @Override
        public String toString() {
            return this.getClass().getSimpleName() + "(" + right + ")";
        }
    }

    public static class AssignmentMissingOperand extends KItem {
        private final Id id;

        private AssignmentMissingOperand(Id id) {
            this.id = id;
        }

        public Id getId() {
            return id;
        }

        @Override
        public String toString() {
            return this.getClass().getSimpleName() + "(" + id + ")";
        }
    }

    public static class IfMissingCondition extends KItem {
        private final Block ithen;
        private final Block ielse;

        private IfMissingCondition(Block ithen, Block ielse) {
            this.ithen = ithen;
            this.ielse = ielse;
        }

        public Block getIthen() {
            return ithen;
        }

        public Block getIelse() {
            return ielse;
        }

        @Override
        public String toString() {
            return this.getClass().getSimpleName() + "(" + ithen + ", " + ielse + ")";
        }
    }
}
