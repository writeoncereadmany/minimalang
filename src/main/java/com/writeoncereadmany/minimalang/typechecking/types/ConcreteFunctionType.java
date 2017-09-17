package com.writeoncereadmany.minimalang.typechecking.types;

import co.unruly.control.HigherOrderFunctions;
import co.unruly.control.result.Result;
import com.writeoncereadmany.minimalang.typechecking.Type;
import com.writeoncereadmany.minimalang.typechecking.TypeError;
import com.writeoncereadmany.minimalang.typechecking.Types;

import java.util.List;
import java.util.stream.Collectors;

import static co.unruly.control.result.Introducers.ifType;
import static co.unruly.control.result.Match.matchValue;
import static co.unruly.control.result.Resolvers.collapse;
import static co.unruly.control.result.Result.failure;
import static co.unruly.control.result.Result.success;
import static co.unruly.control.result.Transformers.onSuccess;
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
        return types.resolve(other).either(
            otherType -> matchValue(otherType,
                    ifType(FunctionType.class, fun -> canBeAssigned(fun, types)))
                .otherwise(obj -> singletonList(new TypeError("Cannot assign an object type to a function type"))),
            typeErrors -> typeErrors
        );
    }

    private List<TypeError> canBeAssigned(FunctionType other, Types types) {
        return other
            .returnType(this.parameterTypes, types)
            .then(onSuccess(retType -> retType.assign(this.returnType, types)))
            .then(collapse());
    }
}
