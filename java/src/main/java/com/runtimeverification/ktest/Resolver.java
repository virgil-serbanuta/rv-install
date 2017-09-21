package com.runtimeverification.ktest;

public class Resolver<T extends KThing> implements KThingSource<T> {
    private final String name;
    private final Class<T> type;

    public Resolver(String name, Class<T> type) {
        this.name = name;
        this.type = type;
    }

    public static <T extends KThing> Resolver<T> of(String name, Class<T> type) {
        return new Resolver<>(name, type);
    }
}
