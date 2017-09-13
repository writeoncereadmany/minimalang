package com.writeoncereadmany.minimalang.typechecking;

import co.unruly.control.pair.Maps;
import co.unruly.control.pair.Pair;
import co.unruly.control.result.Result;
import com.writeoncereadmany.minimalang.ast.expressions.Expression;
import com.writeoncereadmany.minimalang.typechecking.types.FunctionType;
import com.writeoncereadmany.minimalang.typechecking.types.InterfaceType;
import com.writeoncereadmany.minimalang.typechecking.types.NamedType;
import com.writeoncereadmany.minimalang.typechecking.types.ObjectType;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static co.unruly.control.Lists.successesOrFailures;
import static co.unruly.control.pair.Maps.entry;
import static co.unruly.control.pair.Maps.mapOf;
import static co.unruly.control.result.Introducers.ifType;
import static co.unruly.control.result.Match.matchValue;
import static co.unruly.control.result.Resolvers.split;
import static co.unruly.control.result.Result.failure;
import static co.unruly.control.result.Result.success;
import static co.unruly.control.result.Transformers.attempt;
import static com.writeoncereadmany.minimalang.ast.expressions.Expression.contextFree;
import static com.writeoncereadmany.minimalang.ast.expressions.Expression.usingContext;
import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

public interface TypeChecker {

    static Expression.Catamorphism<Result<Type, List<TypeError>>, Types> typeChecker(final NamedType numberType, final NamedType stringType, final NamedType successType) {
        return new Expression.Catamorphism<>(
            contextFree(stringLiteral -> success(stringType)),
            contextFree(numberLiteral -> success(numberType)),
            (variable, type, context) -> type.either(
                s -> Pair.of(success(successType), context.withVariable(variable, s)),
                f -> Pair.of(failure(f), context)
            ),
            usingContext((variable, context) -> context.typeOf(variable)),
            contextFree(fields -> {
                Pair<List<Pair<String, Type>>, List<Pair<String, List<TypeError>>>> fieldsWhichExist = fields.entrySet()
                        .stream()
                        .<Result<Pair<String, Type>, Pair<String, List<TypeError>>>>map(entry -> entry.getValue().either(
                            type -> success(entry(entry.getKey(), type)),
                            typeErrors -> failure(entry(entry.getKey(), typeErrors))
                        )).collect(split());
                if(fieldsWhichExist.right.isEmpty()) {
                    return success(new ObjectType(fieldsWhichExist.left.stream().collect(Maps.toMap())));
                } else {
                    return failure(fieldsWhichExist.right.stream().map(Pair::right).flatMap(List::stream).collect(toList()));
                }
            }),
            usingContext((type, field, context) -> type
                .then(attempt(context::resolve))
                .then(attempt(t -> matchValue(t,
                    ifType(InterfaceType.class, it -> it.getField(field))
                ).otherwise(f -> failure(singletonList(new TypeError(format("Type %s is not an object type", t)))))))
            ),
            // this is a pretty difficult case
            contextFree((params, body) -> null),
            // this one's quite complicated too
            usingContext((function, args, cata, types) -> successesOrFailures(args)
                .either(
                    arguments -> function.then(attempt(t ->
                        matchValue(t,
                            ifType(FunctionType.class, f -> f.returnType(arguments, types))
                        ).otherwise(obj -> failure(singletonList(new TypeError(format("Type %s is not callable", obj))))))),
                    failures -> failure(failures.stream().flatMap(List::stream).collect(toList())))
            ),
            contextFree(expressions -> null)
        );
    }
}
