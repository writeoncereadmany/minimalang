package com.writeoncereadmany.minimalang.typechecking.types;

import co.unruly.control.HigherOrderFunctions;
import co.unruly.control.result.Result;
import com.writeoncereadmany.minimalang.typechecking.Type;
import com.writeoncereadmany.minimalang.typechecking.TypeError;
import com.writeoncereadmany.minimalang.typechecking.Types;

import java.util.List;
import java.util.stream.Collectors;

import static co.unruly.control.result.Result.failure;
import static co.unruly.control.result.Result.success;
import static java.lang.String.format;
import static java.util.Collections.singletonList;

public class ConcreteFunctionType implements FunctionType {

    private final List<Type> parameterTypes;
    private final Type returnType;

    public ConcreteFunctionType(List<Type> parameterTypes, Type returnType) {
        this.parameterTypes = parameterTypes;
        this.returnType = returnType;
    }

    @Override
    public Result<Type, List<TypeError>> returnType(List<Type> argumentTypes, Types types) {
        if(argumentTypes.size() != parameterTypes.size()) {
            return failure(singletonList(new TypeError(format("Arity mismatch: expected %d arguments, got %d", parameterTypes.size(), argumentTypes.size()))));
        }

        List<TypeError> typeErrors = HigherOrderFunctions
                .zip(parameterTypes.stream(), argumentTypes.stream())
                .map(pair -> pair.left.assign(pair.right, types))
                .flatMap(List::stream)
                .collect(Collectors.toList());

        if(!typeErrors.isEmpty()) {
            return failure(typeErrors);
        }

        return success(returnType);
    }

    @Override
    public List<TypeError> assign(Type other, Types types) {
        return null;
    }
}
