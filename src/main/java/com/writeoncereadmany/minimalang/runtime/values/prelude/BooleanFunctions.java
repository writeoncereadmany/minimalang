package com.writeoncereadmany.minimalang.runtime.values.prelude;

import com.writeoncereadmany.minimalang.ast.Expression;
import com.writeoncereadmany.minimalang.runtime.Environment;
import com.writeoncereadmany.minimalang.runtime.values.FunctionValue;
import com.writeoncereadmany.minimalang.runtime.values.Value;

import java.util.Collections;
import java.util.List;

public enum BooleanFunctions implements FunctionValue {

    TRUE("true") {
        @Override
        public Value invoke(List<Value> arguments, Expression.Catamorphism<Value, Environment> cata) {
            return ((FunctionValue)arguments.get(0)).invoke(Collections.emptyList(), cata);
        }
    }, FALSE("false") {
        @Override
        public Value invoke(List<Value> arguments, Expression.Catamorphism<Value, Environment> cata) {
            return ((FunctionValue)arguments.get(1)).invoke(Collections.emptyList(), cata);
        }
    };

    private final String name;

    BooleanFunctions(String name) {
        this.name = name;
    }

    @Override
    public String show() {
        return name;
    }
}
