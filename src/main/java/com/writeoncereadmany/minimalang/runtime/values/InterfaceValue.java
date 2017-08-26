package com.writeoncereadmany.minimalang.runtime.values;

public interface InterfaceValue extends Value {

    Value field(String fieldName);
}
