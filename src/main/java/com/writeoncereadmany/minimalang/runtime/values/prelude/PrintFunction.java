package com.writeoncereadmany.minimalang.runtime.values.prelude;

import co.unruly.control.result.Resolvers;
import co.unruly.control.result.Result;
import com.writeoncereadmany.minimalang.ast.expressions.Expression;
import com.writeoncereadmany.minimalang.ast.expressions.Expression.Catamorphism;
import com.writeoncereadmany.minimalang.runtime.EvaluationException;
import com.writeoncereadmany.minimalang.runtime.values.FunctionValue;
import com.writeoncereadmany.minimalang.runtime.values.StringValue;
import com.writeoncereadmany.minimalang.runtime.values.Value;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import static co.unruly.control.ApplicableWrapper.startWith;
import static co.unruly.control.result.Introducers.castTo;
import static co.unruly.control.result.Transformers.*;
import static com.writeoncereadmany.minimalang.runtime.values.prelude.SuccessValue.SUCCESS;
import static com.writeoncereadmany.minimalang.util.ListUtils.extractSingleValue;

/**
 * Created by tomj on 22/08/2017.
 */
public class PrintFunction implements FunctionValue {

    private final Consumer<String> printChannel;

    public PrintFunction(Consumer<String> printChannel) {
        this.printChannel = printChannel;
    }

    @Override
    public String show() {
        return "Function print[]";
    }

    @Override
    public Value invoke(List<Value> arguments, Catamorphism<Value, Map<String, Value>> cata, Map<String, Value> context) {
        return startWith(arguments)
            .then(extractSingleValue())
            .then(onFailure(args -> "Arity error: expected 1 argument, got " + args.size()))
            .then(onSuccess(string -> string.show()))
            .then(onSuccessDo(printChannel::accept))
            .then(onSuccess(__ -> SUCCESS))
            .then(Resolvers.getOrThrow(EvaluationException::new));
    }
}
