package com.writeoncereadmany.minimalang.typechecking;

import co.unruly.control.result.Result;

import java.util.Map;

public class ObjectType implements InterfaceType {

    private final Map<String, Type> fields;

    public ObjectType(Map<String, Type> fields) {
        this.fields = fields;
    }

    @Override
    public Result<Type, TypeError> getField(String name) {
        return Result.success(fields.get(name));
    }
}
