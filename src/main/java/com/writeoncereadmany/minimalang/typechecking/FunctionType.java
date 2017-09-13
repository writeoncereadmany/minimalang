package com.writeoncereadmany.minimalang.typechecking;

import co.unruly.control.result.Result;

import java.util.List;

public class FunctionType implements Type {

    public Result<Type, TypeError> returnType(List<Type> args) {
        return null;
    }
}
