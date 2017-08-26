package com.writeoncereadmany.minimalang.runtime.values;

import java.util.Map;

public class ObjectValue implements InterfaceValue {

    private final Map<String, Value> fields;

    public ObjectValue(Map<String, Value> fields) {
        this.fields = fields;
    }

    public Value field(String fieldName) {
        return fields.get(fieldName);
    }

    @Override
    public String show() {
        return null;
    }
}
