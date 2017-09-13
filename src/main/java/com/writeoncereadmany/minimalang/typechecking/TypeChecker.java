package com.writeoncereadmany.minimalang.typechecking;

import co.unruly.control.pair.Pair;
import co.unruly.control.result.Result;
import co.unruly.control.result.TypeOf;
import com.writeoncereadmany.minimalang.ast.expressions.Expression;
import com.writeoncereadmany.minimalang.ast.expressions.Expression.Catamorphism;
import com.writeoncereadmany.minimalang.typechecking.types.FunctionType;
import com.writeoncereadmany.minimalang.typechecking.types.InterfaceType;
import com.writeoncereadmany.minimalang.typechecking.types.NamedType;
import com.writeoncereadmany.minimalang.typechecking.types.ObjectType;

import java.util.List;
import java.util.Map;

import static co.unruly.control.Lists.successesOrFailures;
import static co.unruly.control.pair.Maps.toMap;
import static co.unruly.control.pair.Pairs.anyFailures;
import static co.unruly.control.result.Introducers.ifType;
import static co.unruly.control.result.Match.matchValue;
import static co.unruly.control.result.Resolvers.split;
import static co.unruly.control.result.Result.failure;
import static co.unruly.control.result.Result.success;
import static co.unruly.control.result.Transformers.attempt;
import static co.unruly.control.result.Transformers.onFailure;
import static co.unruly.control.result.Transformers.onSuccess;
import static co.unruly.control.result.TypeOf.using;
import static com.writeoncereadmany.minimalang.ast.expressions.Expression.contextFree;
import static com.writeoncereadmany.minimalang.ast.expressions.Expression.usingContext;
import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

public interface TypeChecker {

    static Catamorphism<Result<Type, List<TypeError>>, Types> typeChecker(
        final NamedType numberType,
        final NamedType stringType,
        final NamedType successType)
    {
        return new Catamorphism<>(
            stringLiteral(stringType),
            numberLiteral(numberType),
            variableDeclaration(successType),
            variable(),
            objectLiteral(),
            access(),
            functionExpression(),
            functionCall(),
            group()
        );
    }

    static Expression.Interpreter<String, Result<Type, List<TypeError>>, Types> stringLiteral(NamedType stringType) {
        return contextFree(stringLiteral -> success(stringType));
    }

    static Expression.Interpreter<String, Result<Type, List<TypeError>>, Types> numberLiteral(NamedType numberType) {
        return contextFree(stringLiteral -> success(numberType));
    }

    static Expression.BiInterpreter<String, Result<Type, List<TypeError>>, Result<Type, List<TypeError>>, Types> variableDeclaration(NamedType successType) {
        return (variable, type, context) -> type.either(
            s -> Pair.of(success(successType), context.withVariable(variable, s)),
            f -> Pair.of(failure(f), context)
        );
    }

    static Expression.Interpreter<String, Result<Type, List<TypeError>>, Types> variable() {
        return usingContext((variable, context) -> context.typeOf(variable));
    }

    static Expression.Interpreter<Map<String, Result<Type, List<TypeError>>>, Result<Type, List<TypeError>>, Types> objectLiteral() {
        return contextFree(fields ->
            anyFailures(fields
                .entrySet()
                .stream()
                .map(TypeChecker::liftResults)
                .collect(split()))
            .then(onSuccess(validFields -> new ObjectType(validFields.stream().collect(toMap()))))
            .then(using(TypeOf.<Type>forSuccesses()))
            .then(onFailure(invalidFields -> invalidFields.stream().map(Pair::right).flatMap(List::stream).collect(toList()))));
    }

    static Expression.BiInterpreter<Result<Type, List<TypeError>>, String, Result<Type, List<TypeError>>, Types> access() {
        return usingContext((type, field, context) -> type
            .then(attempt(context::resolve))
            .then(attempt(t -> matchValue(t,
                ifType(InterfaceType.class, it -> it.getField(field))
            ).otherwise(f -> failure(singletonList(new TypeError(format("Type %s is not an object type", t)))))))
        );
    }

    static Expression.BiInterpreter<List<String>, Expression, Result<Type, List<TypeError>>, Types> functionExpression() {
        return contextFree((params, body) -> null);
    }

    static Expression.TriInterpreter<Result<Type, List<TypeError>>, List<Result<Type, List<TypeError>>>, Catamorphism<Result<Type, List<TypeError>>, Types>, Result<Type, List<TypeError>>, Types> functionCall() {
        return usingContext((function, args, cata, types) -> successesOrFailures(args)
            .either(
                arguments -> function.then(attempt(t ->
                    matchValue(t,
                        ifType(FunctionType.class, f -> f.returnType(arguments, types))
                    ).otherwise(obj -> failure(singletonList(new TypeError(format("Type %s is not callable", obj))))))),
                failures -> failure(failures.stream().flatMap(List::stream).collect(toList())))
        );
    }

    static Expression.Interpreter<List<Result<Type, List<TypeError>>>, Result<Type, List<TypeError>>, Types> group() {
        return contextFree(expressions -> null);
    }

    static <K, S, F> Result<Pair<K, S>, Pair<K, F>> liftResults(Map.Entry<K, Result<S, F>> entry) {
        final K key = entry.getKey();
        return entry.getValue()
            .then(onSuccess(value -> Pair.of(key, value)))
            .then(onFailure(error -> Pair.of(key, error)));
    }
}
