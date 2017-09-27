package com.runtimeverification.ktest;

import com.runtimeverification.ktest.nonterminals.Id;

import java.util.Arrays;
import java.util.Objects;

public class KThing {
    public final Type type;
    public final KThing child1;
    public final KThing child2;
    public final KThing child3;

    public enum Type {
        BLOCK_STMT, AEXP_ADD, AEXP_DIV, AEXP_ID, AEXP_INT, BEXP_AND, BEXP_NOT, BEXP_LESS_OR_EQUALS, BEXP_BOOL, BOOL, ID, IDS_EMPTY, IDS_NON_EMPTY, INT, KITEM_DIVISION_LEFT_MISSING, KITEM_DIVISION_RIGHT_MISSING, KITEM_IF_MISSING_CONDITION, KITEM_ASSIGNMENT_MISSING_OPERAND, KITEM_AND_LEFT_MISSING, KITEM_NOT_MISSING_OPERAND, KITEM_LESS_OR_EQUALS_RIGHT_MISSING, KITEM_LESS_OR_EQUALS_LEFT_MISSING, KITEM_ADDITION_RIGHT_MISSING, KITEM_ADDITION_LEFT_MISSING, PGM, STMT_WHILE, STMT_IF, STMT_SEQUENCE, STMT_ASSIGN, STMT_BLOCK, BLOCK_EMPTY
    }

    public KThing(Type type) {
        this.type = type;
        this.child1 = null;
        this.child2 = null;
        this.child3 = null;
    }

    public KThing(Type type, KThing child1) {
        this.type = type;
        this.child1 = child1;
        this.child2 = null;
        this.child3 = null;
    }

    public KThing(Type type, KThing child1, KThing child2) {
        this.type = type;
        this.child1 = child1;
        this.child2 = child2;
        this.child3 = null;
    }

    public KThing(Type type, KThing child1, KThing child2, KThing child3) {
        this.type = type;
        this.child1 = child1;
        this.child2 = child2;
        this.child3 = child3;
    }

    public boolean getAsBool() {
        throw new IllegalStateException("getAsBool not defined for " + type + ".");
    }

    public int getAsInt() {
        throw new IllegalStateException("getAsInt not defined for " + type + ".");
    }

    @Override
    public String toString() {
        StringBuilder retv = new StringBuilder();
        retv.append(type);
        retv.append("(");
        if (child1 != null) {
            retv.append(child1);
            if (child2 != null) {
                retv.append(", ");
                retv.append(child2);
                if (child3 != null) {
                    retv.append(", ");
                    retv.append(child3);
                }
            }
        }
        retv.append(")");
        return retv.toString();
    }

    @Override
    public int hashCode() {
        long hash = type.hashCode();
        if (child1 != null) {
            hash = hash * 41 + child1.hashCode();
            if (child2 != null) {
                hash = hash * 41 + child2.hashCode();
                if (child3 != null) {
                    hash = hash * 41 + child3.hashCode();
                }
            }
        }
        return (int)hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof KThing)) {
            return false;
        }
        KThing other = (KThing) obj;
        return type == other.type
                && Objects.equals(child1, other.child1)
                && Objects.equals(child2, other.child2)
                && Objects.equals(child3, other.child3);
    }
}
