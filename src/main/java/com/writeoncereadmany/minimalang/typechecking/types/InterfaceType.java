package com.writeoncereadmany.minimalang.typechecking.types;

import co.unruly.control.result.Result;
import com.writeoncereadmany.minimalang.typechecking.Type;
import com.writeoncereadmany.minimalang.typechecking.TypeError;

import java.util.List;

public interface InterfaceType extends Type {

    Result<Type, List<TypeError>> getField(String name);
}
