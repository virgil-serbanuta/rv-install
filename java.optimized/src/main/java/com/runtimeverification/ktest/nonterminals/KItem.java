package com.runtimeverification.ktest.nonterminals;

import com.runtimeverification.ktest.KThing;

public class KItem {
    public static KThing divisionLeftMissing(KThing right) {
        return new KThing(KThing.Type.KITEM_DIVISION_LEFT_MISSING, right);
    }
    public static KThing divisionRightMissing(KThing left) {
        return new KThing(KThing.Type.KITEM_DIVISION_RIGHT_MISSING, left);
    }

    public static KThing additionLeftMissing(KThing right) {
        return new KThing(KThing.Type.KITEM_ADDITION_LEFT_MISSING, right);
    }
    public static KThing additionRightMissing(KThing left) {
        return new KThing(KThing.Type.KITEM_ADDITION_RIGHT_MISSING, left);
    }

    public static KThing lessOrEqualsLeftMissing(KThing right) {
        return new KThing(KThing.Type.KITEM_LESS_OR_EQUALS_LEFT_MISSING, right);
    }
    public static KThing lessOrEqualsRightMissing(KThing left) {
        return new KThing(KThing.Type.KITEM_LESS_OR_EQUALS_RIGHT_MISSING, left);
    }

    public static KThing notMissingOperand() {
        return new KThing(KThing.Type.KITEM_NOT_MISSING_OPERAND);
    }

    public static KThing andLeftMissing(KThing right) {
        return new KThing(KThing.Type.KITEM_AND_LEFT_MISSING, right);
    }

    public static KThing assignmentMissingOperand(KThing left) {
        return new KThing(KThing.Type.KITEM_ASSIGNMENT_MISSING_OPERAND, left);
    }

    public static KThing ifMissingCondition(KThing ithen, KThing ielse) {
        return new KThing(KThing.Type.KITEM_IF_MISSING_CONDITION, ithen, ielse);
    }
}
