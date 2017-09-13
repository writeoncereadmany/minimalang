package com.writeoncereadmany.minimalang.typechecking;

import co.unruly.control.result.Result;

public class DataType implements InterfaceType {

    public final String name;

    public DataType(String name) {
        this.name = name;
    }

    @Override
    public Result<Type, TypeError> getField(String name) {
        return null;
    }
}
