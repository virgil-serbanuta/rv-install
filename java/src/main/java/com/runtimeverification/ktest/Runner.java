package com.runtimeverification.ktest;

import java.util.List;
import java.util.Optional;

public class Runner {
    public static Configuration run(Configuration configuration, List<Rule> rules) {
        while (true) {
            Optional<Configuration> nextConfiguration = step(configuration, rules);
            if (!nextConfiguration.isPresent()) {
                return configuration;
            }
            configuration = nextConfiguration.get();
        }
    }

    private static Optional<Configuration> step(Configuration configuration, List<Rule> rules) {
        for (Rule rule : rules) {
            Optional<Configuration> result = configuration.applyRule(rule);
            if (result.isPresent()) {
                return result;
            }
        }
        return Optional.empty();
    }
}
