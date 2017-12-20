package com.writeoncereadmany.minimalang.typechecking;

import co.unruly.control.pair.Maps;
import co.unruly.control.pair.Pair;
import co.unruly.control.result.Result;
import co.unruly.control.result.TypeOf;
import com.writeoncereadmany.minimalang.ast.expressions.Expression;
import com.writeoncereadmany.minimalang.ast.expressions.Expression.Catamorphism;
import com.writeoncereadmany.minimalang.ast.expressions.Introduction;
import com.writeoncereadmany.minimalang.typechecking.types.FunctionType;
import com.writeoncereadmany.minimalang.typechecking.types.InterfaceType;
import com.writeoncereadmany.minimalang.typechecking.types.NamedType;
import com.writeoncereadmany.minimalang.typechecking.types.ObjectType;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static co.unruly.control.ApplicableWrapper.startWith;
import static co.unruly.control.Lists.successesOrFailures;
import static co.unruly.control.pair.Pairs.anyFailures;
import static co.unruly.control.pair.Pairs.onLeft;
import static co.unruly.control.result.Introducers.ifType;
import static co.unruly.control.result.Match.matchValue;
import static co.unruly.control.result.Resolvers.collapse;
import static co.unruly.control.result.Resolvers.split;
import static co.unruly.control.result.Result.failure;
import static co.unruly.control.result.Result.success;
import static co.unruly.control.result.Transformers.*;
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

    static Expression.BiInterpreter<Introduction, Result<Type, List<TypeError>>, Result<Type, List<TypeError>>, Types> variableDeclaration(NamedType successType) {
        return (variable, type, context) -> type.either(
            s -> startWith(s)
                .then(checkAgainstAnnotations(variable, context))
                .either(
                    succ -> Pair.of(success(successType), context.withVariable(variable.name, succ)),
                    fail -> Pair.of(failure(fail), context)),
            f -> Pair.of(failure(f), context)
        );
    }

    static Expression.Interpreter<String, Result<Type, List<TypeError>>, Types> variable() {
        return usingContext((variable, context) -> context.typeOf(variable));
    }

    static Expression.Interpreter<Map<Introduction, Result<Type, List<TypeError>>>, Result<Type, List<TypeError>>, Types> objectLiteral() {
        return usingContext((fields, types) -> anyFailures(fields
            .entrySet()
            .stream()
            .map(TypeChecker::liftResults)
            .map(attempt(pair -> startWith(pair.right)
                .then(checkAgainstAnnotations(pair.left, types))
                .then(onSuccess(s -> Pair.of(pair.left, s)))
                .then(onFailure(f -> Pair.of(pair.left, f)))))
            .collect(split())
        ).then(onSuccess(validFields -> new ObjectType(validFields
            .stream()
            .map(onLeft(i -> i.name))
            .collect(Maps.toMap())))
        ).then(using(TypeOf.<Type>forSuccesses())
        ).then(onFailure(invalidFields -> invalidFields
            .stream()
            .map(Pair::right)
            .flatMap(List::stream)
            .collect(toList()))));
    }

    static Expression.BiInterpreter<Result<Type, List<TypeError>>, String, Result<Type, List<TypeError>>, Types> access() {
        return usingContext((type, field, context) -> type
            .then(attempt(context::resolve))
            .then(attempt(t -> matchValue(t,
                ifType(InterfaceType.class, it -> it.getField(field))
            ).otherwise(f -> failure(singletonList(new TypeError(format("Type %s is not an object type", t)))))))
        );
    }

    /**
     * TODO
     */
    static Expression.BiInterpreter<List<Introduction>, Expression, Result<Type, List<TypeError>>, Types> functionExpression() {
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

    /**
     * TODO
     */
    static Expression.Interpreter<List<Result<Type, List<TypeError>>>, Result<Type, List<TypeError>>, Types> group() {
        return contextFree(expressions -> null);
    }

    static <K, S, F> Result<Pair<K, S>, Pair<K, F>> liftResults(Map.Entry<K, Result<S, F>> entry) {
        final K key = entry.getKey();
        return entry.getValue()
            .then(onSuccess(value -> Pair.of(key, value)))
            .then(onFailure(error -> Pair.of(key, error)));
    }

    static Function<Type, Result<Type, List<TypeError>>> checkAgainstAnnotations(Introduction introduction, Types types) {
        return type -> {
            List<TypeError> errors = introduction
                .annotations
                .stream()
                .map(types::named)
                .map(onSuccess(t -> t.assign(type, types)))
                .map(collapse())
                .flatMap(List::stream)
                .collect(toList());
            return errors.isEmpty() ? success(type) : failure(errors);
        };
    }
}