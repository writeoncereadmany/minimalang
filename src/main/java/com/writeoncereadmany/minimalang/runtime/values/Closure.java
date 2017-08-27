package com.writeoncereadmany.minimalang.runtime.values;

import co.unruly.control.pair.Pair;
import com.writeoncereadmany.minimalang.ast.expressions.Expression;
import com.writeoncereadmany.minimalang.ast.expressions.Expression.Catamorphism;
import com.writeoncereadmany.minimalang.runtime.EvaluationException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static co.unruly.control.HigherOrderFunctions.zip;
import static com.writeoncereadmany.minimalang.util.MapUtils.immutablePut;

public class Closure implements FunctionValue {

    private final List<String> parameters;
    private final Expression body;
    private final Map<String, Value> context;

    public Closure(List<String> parameters, Expression body, Map<String, Value> context) {
        this.parameters = parameters;
        this.body = body;
        this.context = context;
    }


    @Override
    public Value invoke(List<Value> arguments, Catamorphism<Value, Map<String, Value>> cata) {
        if(arguments.size() != parameters.size()) {
            throw new EvaluationException("Wrong number of parameters provided");
        }

        Map<String, Value> functionContext = context;
        for(Pair<String, Value> params : zip(parameters.stream(), arguments.stream()).collect(Collectors.toList())) {
            functionContext = immutablePut(functionContext, params.left, params.right);
        }

        return body.fold(cata, functionContext).left;
    }

    @Override
    public String show() {
        return "Closure";
    }
}
