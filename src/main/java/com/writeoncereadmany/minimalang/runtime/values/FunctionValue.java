package com.writeoncereadmany.minimalang.runtime.values;

import com.writeoncereadmany.minimalang.ast.expressions.Expression;
import com.writeoncereadmany.minimalang.ast.expressions.Expression.Catamorphism;

import java.util.List;
import java.util.Map;

/**
 * Created by tomj on 22/08/2017.
 */
public interface FunctionValue extends Value {

    Value invoke(List<Value> arguments, Catamorphism<Value, Map<String, Value>> cata);
}
