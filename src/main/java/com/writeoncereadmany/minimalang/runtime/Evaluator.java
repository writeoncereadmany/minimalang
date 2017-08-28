package com.writeoncereadmany.minimalang.runtime;

import co.unruly.control.PartialApplication.TriFunction;
import co.unruly.control.pair.Pair;
import co.unruly.control.result.Resolvers;
import com.writeoncereadmany.minimalang.ast.expressions.Expression;
import com.writeoncereadmany.minimalang.ast.expressions.Expression.Catamorphism;
import com.writeoncereadmany.minimalang.runtime.values.*;

import java.util.function.BiFunction;
import java.util.function.Function;

import static co.unruly.control.ApplicableWrapper.startWith;
import static co.unruly.control.result.Introducers.castTo;
import static co.unruly.control.result.Transformers.onSuccess;
import static com.writeoncereadmany.minimalang.runtime.values.prelude.SuccessValue.SUCCESS;

/**
 * Created by tomj on 22/08/2017.
 */
public interface Evaluator {

    static Catamorphism<Value, Environment> evaluator() {

        return new Catamorphism<>(
            contextFree(StringValue::new),
            contextFree(NumberValue::new),
            (name, value, context) -> Pair.of(SUCCESS, context.with(name, value)),
            usingContext((name, context) -> context.get(name)),
            contextFree(ObjectValue::new),
            contextFree((object, field) -> startWith(object)
                .then(castTo(InterfaceValue.class))
                .then(onSuccess(obj -> obj.field(field)))
                .then(Resolvers.getOrThrow(obj -> new EvaluationException("Can only access fields of objects: got a " + obj.getClass())))),
            usingContext((parameters, body, context) -> new Closure(parameters, body, context)),
            (function, arguments, cata, context) -> startWith(function)
                .then(castTo(FunctionValue.class))
                .then(onSuccess(f -> f.invoke(arguments, cata)))
                .then(onSuccess(result -> Pair.of(result, context)))
                .then(Resolvers.getOrThrow(__ -> new EvaluationException("Can only execute functions"))),
            contextFree(expressions -> expressions.get(expressions.size() - 1))
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

    static <A, B, T, C> Expression.BiInterpreter<A, B, T, C> usingContext(TriFunction<A, B, C, T> contextUsingFunction) {
        return (a, b, c) -> Pair.of(contextUsingFunction.apply(a, b, c), c);
    }

}
