package com.writeoncereadmany.minimalang.typechecking;

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
import static com.writeoncereadmany.minimalang.ast.expressions.Expression.contextFree;
import static com.writeoncereadmany.minimalang.ast.expressions.Expression.usingContext;
import static java.lang.String.format;

public interface TypeChecker {

    static Expression.Catamorphism<Result<Type, TypeError>, Types> typeChecker(final NamedType numberType, final NamedType stringType, final NamedType successType) {
        return new Expression.Catamorphism<>(
            contextFree(stringLiteral -> success(stringType)),
            contextFree(numberLiteral -> success(numberType)),
            (variable, type, context) -> type.either(
                s -> Pair.of(success(successType), context.withVariable(variable, s)),
                f -> Pair.of(failure(f), context)
            ),
            usingContext((variable, context) -> context.typeOf(variable)),
            // often these will be unknown
            contextFree(fields -> null),
            usingContext((type, field, context) -> type
                .then(attempt(t -> matchValue(t,
                    ifType(InterfaceType.class, it -> it.getField(field))
                ).otherwise(f -> failure(new TypeError(format("Type %s does not have a field %s", t, field))))))
            ),
            // this is a pretty difficult case
            contextFree((params, body) -> null),
            // this one's quite complicated too
            contextFree((function, args, cata) -> null),
            contextFree(expressions -> null)
        );
    }


}
