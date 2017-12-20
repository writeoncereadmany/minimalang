package com.writeoncereadmany.minimalang.runtime.values;

import com.writeoncereadmany.minimalang.ast.Expression.Catamorphism;
import com.writeoncereadmany.minimalang.runtime.Environment;

import java.util.List;

/**
 * Created by tomj on 22/08/2017.
 */
public interface FunctionValue extends Value {

    Value invoke(List<Value> arguments, Catamorphism<Value, Environment> cata);
}
