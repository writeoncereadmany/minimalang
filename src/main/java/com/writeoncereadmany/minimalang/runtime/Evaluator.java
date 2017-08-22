package com.writeoncereadmany.minimalang.runtime;

import co.unruly.control.pair.Pair;
import co.unruly.control.result.Resolvers;
import com.writeoncereadmany.minimalang.ast.expressions.Expression;
import com.writeoncereadmany.minimalang.runtime.values.FunctionValue;
import com.writeoncereadmany.minimalang.runtime.values.StringValue;
import com.writeoncereadmany.minimalang.runtime.values.Value;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import static co.unruly.control.ApplicableWrapper.startWith;
import static co.unruly.control.result.Introducers.castTo;
import static co.unruly.control.result.Transformers.onSuccess;

/**
 * Created by tomj on 22/08/2017.
 */
public interface Evaluator {

    static Expression.Catamorphism<Value, Map<String, Value>> evaluator() {
        return new Expression.Catamorphism<>(
            (function, arguments, context) -> startWith(function)
                    .then(castTo(FunctionValue.class))
                    .then(onSuccess(f -> f.invoke(arguments)))
                    .then(onSuccess(v -> Pair.of(v, context)))
                    .then(Resolvers.getOrThrow(__ -> new EvaluationException("Can only execute functions"))),
            contextFree(StringValue::new),
            (name, context) -> Pair.of(context.get(name), context),
            contextFree(values -> values.get(values.size() - 1))
        );
    }

    static <E, T, C> BiFunction<E, C, Pair<T, C>> contextFree(Function<E, T> contextFreeFunction) {
        return (e, c) -> Pair.of(contextFreeFunction.apply(e), c);
    }
}
