package com.runtimeverification.ktest;

import com.runtimeverification.ktest.nonterminals.Bool;
import com.runtimeverification.ktest.nonterminals.Ids;
import com.runtimeverification.ktest.nonterminals.KResult;
import com.runtimeverification.ktest.rule.RuleTerm;
import com.runtimeverification.ktest.rule.operations.Operation;

import java.util.Optional;

public class Builtins {
    public static Operation<Bool> is(Class<KResult> kClass, Resolver<KThing> thing) {
        return new Operation<Bool>() {
            @Override
            public Optional<Bool> resolve(Context context) {
                Optional<KThing> maybeResolved = thing.resolve(context);
                if (!maybeResolved.isPresent()) {
                    return Optional.of(Bool.of("false"));
                }
                KThing resolved = maybeResolved.get();
                return Optional.of(Bool.of(Boolean.toString(kClass.isInstance(resolved))));
            }
        };
    }

    public static RuleTerm empty(Class<Ids> idsClass) {
        return new RuleTerm() {
        };
    }
}
