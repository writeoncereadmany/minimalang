package com.writeoncereadmany.minimalang.typechecking;

import co.unruly.control.result.Result;

public interface InterfaceType extends Type {

    Result<Type, TypeError> getField(String name);
}
