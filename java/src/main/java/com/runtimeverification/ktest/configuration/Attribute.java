package com.runtimeverification.ktest.configuration;

public class Attribute {
    private final String name;
    private final String value;

    private Attribute(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public static Attribute of(String name, String value) {
        return new Attribute(name, value);
    }
}
