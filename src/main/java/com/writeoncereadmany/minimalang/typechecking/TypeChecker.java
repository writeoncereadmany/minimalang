package com.writeoncereadmany.minimalang.typechecking;

import co.unruly.control.PartialApplication;
import co.unruly.control.PartialApplication.TriFunction;
import co.unruly.control.pair.Pair;
import co.unruly.control.result.Result;
import com.writeoncereadmany.minimalang.ast.expressions.Expression;

import java.util.function.BiFunction;
import java.util.function.Function;

import static co.unruly.control.result.Introducers.ifType;
import static co.unruly.control.result.Match.matchValue;
import static co.unruly.control.result.Result.failure;
import static co.unruly.control.result.Result.success;
import static co.unruly.control.result.Transformers.attempt;
import static java.lang.String.format;

public interface TypeChecker {

    static Expression.Catamorphism<Result<Type, TypeError>, Types> typeChecker() {
        return new Expression.Catamorphism<>(
            contextFree(stringLiteral -> success(new NamedType("String"))),
            contextFree(numberLiteral -> success(new NamedType("Number"))),
            (variable, type, context) -> type.either(
                s -> Pair.of(success(new NamedType("Success")), context.withVariable(variable, s)),
                f -> Pair.of(failure(f), context)
            ),
            usingContext((variable, context) -> context.typeOf(variable)),
            contextFree(fields -> null),
            usingContext((type, field, context) -> type
                .then(attempt(t -> matchValue(t,
                    ifType(InterfaceType.class, it -> it.getField(field))
                ).otherwise(f -> failure(new TypeError(format("Type %s does not have a field %s", t, field))))))
            ),
            contextFree((params, body) -> null),
            contextFree((function, args, cata) -> null),
            contextFree(expressions -> null)
        );
    }

    static <E, T, C> Expression.Interpreter<E, T, C> contextFree(Function<E, T> contextFreeFunction) {
        return (e, c) -> Pair.of(contextFreeFunction.apply(e), c);
    }

    static <A, B, T, C> Expression.BiInterpreter<A, B, T, C> contextFree(BiFunction<A, B, T> contextFreeFunction) {
        return (a, b, c) -> Pair.of(contextFreeFunction.apply(a, b), c);
    }

    static <X, Y, Z, T, C> Expression.TriInterpreter<X, Y, Z, T, C> contextFree(TriFunction<X, Y, Z, T> contextFreeFunction) {
        return (x, y, z, c) -> Pair.of(contextFreeFunction.apply(x,y,z), c);
    }

    static <E, T, C> Expression.Interpreter<E, T, C> usingContext(BiFunction<E, C, T> contextUsingFunction) {
        return (e, c) -> Pair.of(contextUsingFunction.apply(e, c), c);
    }

    static <A, B, T, C> Expression.BiInterpreter<A, B, T, C> usingContext(TriFunction<A, B, C, T> contextUsingFunction) {
        return (a, b, c) -> Pair.of(contextUsingFunction.apply(a, b, c), c);
    }
}
