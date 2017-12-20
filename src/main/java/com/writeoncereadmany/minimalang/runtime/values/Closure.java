package com.writeoncereadmany.minimalang.runtime.values;

import co.unruly.control.pair.Pair;
import com.writeoncereadmany.minimalang.ast.Expression;
import com.writeoncereadmany.minimalang.ast.Expression.Catamorphism;
import com.writeoncereadmany.minimalang.runtime.Environment;
import com.writeoncereadmany.minimalang.runtime.EvaluationException;

import java.util.List;
import java.util.stream.Collectors;

import static co.unruly.control.HigherOrderFunctions.zip;

public class Closure implements FunctionValue {

    private final List<String> parameters;
    private final Expression body;
    private final Environment context;

    public Closure(List<String> parameters, Expression body, Environment context) {
        this.parameters = parameters;
        this.body = body;
        this.context = context;
    }


    @Override
    public Value invoke(List<Value> arguments, Catamorphism<Value, Environment> cata) {
        if(arguments.size() != parameters.size()) {
            throw new EvaluationException("Wrong number of parameters provided");
        }

        Environment functionContext = context;
        for(Pair<String, Value> params : zip(parameters.stream(), arguments.stream()).collect(Collectors.toList())) {
            functionContext = functionContext.with(params.left, params.right);
        }

        return body.fold(cata, functionContext).left;
    }

    @Override
    public String show() {
        return "Closure";
    }
}
