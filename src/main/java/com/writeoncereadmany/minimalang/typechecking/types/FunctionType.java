package com.writeoncereadmany.minimalang.typechecking.types;

import co.unruly.control.result.Result;
import com.writeoncereadmany.minimalang.typechecking.Type;
import com.writeoncereadmany.minimalang.typechecking.TypeError;
import com.writeoncereadmany.minimalang.typechecking.Types;

import java.util.List;

public interface FunctionType extends Type {

    Result<Type, TypeError> returnType(List<Type> args, Types types);
}
