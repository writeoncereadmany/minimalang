package com.writeoncereadmany.minimalang.typechecking.types;

import co.unruly.control.result.Result;
import com.writeoncereadmany.minimalang.typechecking.Type;
import com.writeoncereadmany.minimalang.typechecking.TypeError;
import com.writeoncereadmany.minimalang.typechecking.Types;

import java.util.Map;
import java.util.Optional;

public class ObjectType implements InterfaceType {

    private final Map<String, Type> fields;

    public ObjectType(Map<String, Type> fields) {
        this.fields = fields;
    }

    @Override
    public Result<Type, TypeError> getField(String name) {
        return Result.success(fields.get(name));
    }

    @Override
    public Optional<TypeError> assign(Type other, Types types) {
        return null;
    }
}
