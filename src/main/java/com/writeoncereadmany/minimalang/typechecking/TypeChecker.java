package com.writeoncereadmany.minimalang.typechecking;

import co.unruly.control.PartialApplication;
import co.unruly.control.pair.Pair;
import co.unruly.control.result.Result;
import com.writeoncereadmany.minimalang.ast.expressions.Expression;

import java.util.function.BiFunction;
import java.util.function.Function;

import static co.unruly.control.result.Result.failure;
import static co.unruly.control.result.Result.success;

public interface TypeChecker {

    static Expression.Catamorphism<Result<Type, TypeError>, Types> typeChecker() {
        return new Expression.Catamorphism<>(
            contextFree(s -> success(new NamedType("String"))),
            contextFree(s -> success(new NamedType("Number"))),
            (variable, type, context) -> type.either(
                s -> Pair.of(success(new NamedType("Success")), context.withVariable(variable, s)),
                f -> Pair.of(failure(f), context)
            ),
            usingContext((name, context) -> context.typeOf(name)),
            null,
            usingContext((type, field, context) -> null),
            null,
            null,
            null
        );
    }

    static <E, T, C> Expression.Interpreter<E, T, C> contextFree(Function<E, T> contextFreeFunction) {
        return (e, c) -> Pair.of(contextFreeFunction.apply(e), c);
    }

    static <A, B, T, C> Expression.BiInterpreter<A, B, T, C> contextFree(BiFunction<A, B, T> contextFreeFunction) {
        return (a, b, c) -> Pair.of(contextFreeFunction.apply(a, b), c);
    }

    static <E, T, C> Expression.Interpreter<E, T, C> usingContext(BiFunction<E, C, T> contextUsingFunction) {
        return (e, c) -> Pair.of(contextUsingFunction.apply(e, c), c);
    }

    static <A, B, T, C> Expression.BiInterpreter<A, B, T, C> usingContext(PartialApplication.TriFunction<A, B, C, T> contextUsingFunction) {
        return (a, b, c) -> Pair.of(contextUsingFunction.apply(a, b, c), c);
    }
}
