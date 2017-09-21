package com.runtimeverification.ktest.rule.operations;

import com.runtimeverification.ktest.KThing;
import com.runtimeverification.ktest.rule.RuleTerm;

// TODO: Do I need to extend RuleTerm?
public interface Operation<T extends KThing> extends RuleTerm, KThingSource<T> {
}
