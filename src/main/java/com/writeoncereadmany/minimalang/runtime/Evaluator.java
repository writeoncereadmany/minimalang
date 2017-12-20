package com.writeoncereadmany.minimalang.runtime;

import co.unruly.control.pair.Pair;
import co.unruly.control.result.Resolvers;
import com.writeoncereadmany.minimalang.ast.expressions.Expression.Catamorphism;
import com.writeoncereadmany.minimalang.runtime.values.*;

import static co.unruly.control.ApplicableWrapper.startWith;
import static co.unruly.control.result.Introducers.castTo;
import static co.unruly.control.result.Transformers.onSuccess;
import static com.writeoncereadmany.minimalang.ast.expressions.Expression.contextFree;
import static com.writeoncereadmany.minimalang.ast.expressions.Expression.usingContext;
import static com.writeoncereadmany.minimalang.runtime.values.prelude.SuccessValue.SUCCESS;
import static com.writeoncereadmany.minimalang.util.MapUtils.mapKeys;
import static java.util.stream.Collectors.toList;

/**
 * Created by tomj on 22/08/2017.
 */
public interface Evaluator {

    static Catamorphism<Value, Environment> evaluator() {

        return new Catamorphism<>(
            contextFree(StringValue::new),
            contextFree(NumberValue::new),
            (name, value, context) -> Pair.of(SUCCESS, context.with(name.name, value)),
            usingContext((name, context) -> context.get(name)),
            contextFree(fields -> new ObjectValue(mapKeys(i -> i.name, fields))),
            contextFree((object, field) -> startWith(object)
                .then(castTo(InterfaceValue.class))
                .then(onSuccess(obj -> obj.field(field)))
                .then(Resolvers.getOrThrow(obj -> new EvaluationException("Can only access fields of objects: got a " + obj.getClass())))),
            usingContext((parameters, body, cata, context) -> new Closure(
                parameters.stream().map(i -> i.name).collect(toList()),
                body,
                context)),
            (function, arguments, cata, context) -> startWith(function)
                .then(castTo(FunctionValue.class))
                .then(onSuccess(f -> f.invoke(arguments, cata)))
                .then(onSuccess(result -> Pair.of(result, context)))
                .then(Resolvers.getOrThrow(obj -> new EvaluationException("Can only execute functions: got a " + obj.getClass()))),
            contextFree(expressions -> expressions.get(expressions.size() - 1))
        );
    }

}
