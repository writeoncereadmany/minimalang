package com.writeoncereadmany.minimalang.typechecking.types;

import co.unruly.control.HigherOrderFunctions;
import co.unruly.control.Optionals;
import co.unruly.control.result.Result;
import com.writeoncereadmany.minimalang.typechecking.Type;
import com.writeoncereadmany.minimalang.typechecking.TypeError;
import com.writeoncereadmany.minimalang.typechecking.Types;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static co.unruly.control.result.Result.failure;
import static co.unruly.control.result.Result.success;
import static java.lang.String.format;

public class ConcreteFunctionType implements FunctionType {

    private final List<Type> parameterTypes;
    private final Type returnType;

    public ConcreteFunctionType(List<Type> parameterTypes, Type returnType) {
        this.parameterTypes = parameterTypes;
        this.returnType = returnType;
    }

    @Override
    public Result<Type, TypeError> returnType(List<Type> argumentTypes, Types types) {
        if(argumentTypes.size() != parameterTypes.size()) {
            return failure(new TypeError(format("Arity mismatch: expected %d arguments, got %d", parameterTypes.size(), argumentTypes.size())));
        }

        List<TypeError> typeErrors = HigherOrderFunctions
                .zip(argumentTypes.stream(), parameterTypes.stream())
                .map(pair -> pair.left.assign(pair.right, types))
                .flatMap(Optionals::stream)
                .collect(Collectors.toList());

        if(!typeErrors.isEmpty()) {
            return failure(new TypeError(typeErrors.stream().map(TypeError::reason).collect(Collectors.joining(", "))));
        }

        return success(returnType);
    }

    @Override
    public Optional<TypeError> assign(Type other, Types types) {
        return null;
    }
}
