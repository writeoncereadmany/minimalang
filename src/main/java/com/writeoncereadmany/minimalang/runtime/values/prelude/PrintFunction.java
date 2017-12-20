package com.writeoncereadmany.minimalang.runtime.values.prelude;

import co.unruly.control.result.Resolvers;
import com.writeoncereadmany.minimalang.ast.Expression.Catamorphism;
import com.writeoncereadmany.minimalang.runtime.Environment;
import com.writeoncereadmany.minimalang.runtime.EvaluationException;
import com.writeoncereadmany.minimalang.runtime.values.FunctionValue;
import com.writeoncereadmany.minimalang.runtime.values.Value;

import java.util.List;
import java.util.function.Consumer;

import static co.unruly.control.ApplicableWrapper.startWith;
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
    public Value invoke(List<Value> arguments, Catamorphism<Value, Environment> cata) {
        return startWith(arguments)
            .then(extractSingleValue())
            .then(onFailure(args -> "Arity error: expected 1 argument, got " + args.size()))
            .then(onSuccess(string -> string.show()))
            .then(onSuccessDo(printChannel::accept))
            .then(onSuccess(__ -> SUCCESS))
            .then(Resolvers.getOrThrow(EvaluationException::new));
    }
}
