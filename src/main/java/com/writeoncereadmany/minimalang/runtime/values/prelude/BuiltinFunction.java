package com.writeoncereadmany.minimalang.runtime.values.prelude;

import com.writeoncereadmany.minimalang.ast.expressions.Expression;
import com.writeoncereadmany.minimalang.runtime.Environment;
import com.writeoncereadmany.minimalang.runtime.values.FunctionValue;
import com.writeoncereadmany.minimalang.runtime.values.Value;

import java.util.List;
import java.util.function.Function;

public class BuiltinFunction<T> implements FunctionValue {

    private final String name;
    private final Function<List<Value>, T> extractor;
    private final Function<T, Value> implementation;

    public BuiltinFunction(String name, Function<List<Value>, T> extractor, Function<T, Value> implementation) {
        this.name = name;
        this.extractor = extractor;
        this.implementation = implementation;
    }

    @Override
    public Value invoke(List<Value> arguments, Expression.Catamorphism<Value, Environment> cata) {
        return implementation.apply(extractor.apply(arguments));
    }

    @Override
    public String show() {
        return name;
    }
}
