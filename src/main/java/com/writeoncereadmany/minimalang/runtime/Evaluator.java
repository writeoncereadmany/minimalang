package com.writeoncereadmany.minimalang.runtime;

import co.unruly.control.result.Resolvers;
import com.writeoncereadmany.minimalang.ast.expressions.Expression;
import com.writeoncereadmany.minimalang.runtime.values.FunctionValue;
import com.writeoncereadmany.minimalang.runtime.values.StringValue;
import com.writeoncereadmany.minimalang.runtime.values.Value;

import java.util.Map;

import static co.unruly.control.ApplicableWrapper.startWith;
import static co.unruly.control.result.Introducers.castTo;
import static co.unruly.control.result.Transformers.onSuccess;

/**
 * Created by tomj on 22/08/2017.
 */
public interface Evaluator {

    static Expression.Catamorphism<Value> evaluator(Map<String, Value> environment) {
        return new Expression.Catamorphism<>(
            (function, arguments) -> startWith(function)
                    .then(castTo(FunctionValue.class))
                    .then(onSuccess(f -> f.invoke(arguments)))
                    .then(Resolvers.getOrThrow(__ -> new MinimalangExecutionException("Can only execute functions"))),
            StringValue::new,
            environment::get
        );
    }
}
