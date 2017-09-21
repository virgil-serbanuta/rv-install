package com.runtimeverification.ktest.nonterminals;

import com.runtimeverification.ktest.KThing;

public class KItem implements KThing<KItem> {
    public static AdditionRightMissing divisionLeftMissing(KThingSource<Aexp> right) {
        return new AdditionRightMissing(right);
    }
    public static DivisionRightMissing divisionRightMissing(KThingSource<Aexp> left) {
        return new DivisionRightMissing(left);
    }

    public static AdditionLeftMissing additionLeftMissing(KThingSource<Aexp> right) {
        return new AdditionLeftMissing(right);
    }
    public static AdditionRightMissing additionRightMissing(KThingSource<Aexp> left) {
        return new AdditionRightMissing(left);
    }

    public static LessOrEqualsLeftMissing lessOrEqualsLeftMissing(KThingSource<Aexp> right) {
        return new LessOrEqualsLeftMissing(right);
    }
    public static LessOrEqualsRightMissing lessOrEqualsRightMissing(KThingSource<Aexp> left) {
        return new LessOrEqualsRightMissing(left);
    }

    public static NotMissingOperand notMissingOperand() {
        return new NotMissingOperand();
    }

    public static AndLeftMissing andLeftMissing(KThingSource<Bexp> right) {
        return new AndLeftMissing(right);
    }

    public static AssignmentMissingOperand assignmentMissingOperand(KThingSource<KThing> left) {
        return new AssignmentMissingOperand(left);
    }

    public static IfMissingCondition ifMissingCondition(KThingSource<KThing> ithen, KThingSource<KThing> ielse) {
        return new IfMissingCondition(ithen, ielse);
    }

    private static class DivisionLeftMissing extends KItem {
        private final KThingSource<Aexp> right;

        private DivisionLeftMissing(KThingSource<Aexp> right) {
            this.right = right;
        }
    }

    private static class DivisionRightMissing extends KItem {
        private final KThingSource<Aexp> left;

        private DivisionRightMissing(KThingSource<Aexp> left) {
            this.left = left;
        }
    }

    private static class AdditionLeftMissing extends KItem {
        private final KThingSource<Aexp> right;

        private AdditionLeftMissing(KThingSource<Aexp> right) {
            this.right = right;
        }
    }

    private static class AdditionRightMissing extends KItem {
        private final KThingSource<Aexp> left;

        private AdditionRightMissing(KThingSource<Aexp> left) {
            this.left = left;
        }
    }

    private static class LessOrEqualsLeftMissing extends KItem {
        private final KThingSource<Aexp> right;

        private LessOrEqualsLeftMissing(KThingSource<Aexp> right) {
            this.right = right;
        }
    }

    private static class LessOrEqualsRightMissing extends KItem {
        private final KThingSource<Aexp> left;

        private LessOrEqualsRightMissing(KThingSource<Aexp> left) {
            this.left = left;
        }
    }

    private static class NotMissingOperand extends KItem {
        private NotMissingOperand() {
        }
    }

    private static class AndLeftMissing extends KItem {
        private final KThingSource<Bexp> right;

        private AndLeftMissing(KThingSource<Bexp> right) {
            this.right = right;
        }
    }

    private static class AssignmentMissingOperand extends KItem {
        private final KThingSource<KThing> left;

        private AssignmentMissingOperand(KThingSource<KThing> left) {
            this.left = left;
        }
    }

    private static class IfMissingCondition extends KItem {
        private final KThingSource<KThing> ithen;
        private final KThingSource<KThing> ielse;

        private IfMissingCondition(KThingSource<KThing> ithen, KThingSource<KThing> ielse) {
            this.ithen = ithen;
            this.ielse = ielse;
        }
    }
}
