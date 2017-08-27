package com.writeoncereadmany.minimalang.runtime;

import com.writeoncereadmany.minimalang.runtime.values.Value;

import java.util.Map;

import static com.writeoncereadmany.minimalang.util.MapUtils.immutablePut;
import static java.util.Collections.emptyMap;

public class Environment {

    private final Map<String, Value> values;

    public Environment() {
        this(emptyMap());
    }

    public Environment(Map<String, Value> values) {
        this.values = values;
    }

    public Value get(String name) {
        return values.get(name);
    }

    public Environment with(String name, Value value) {
        return new Environment(immutablePut(values, name, value));
    }
}
