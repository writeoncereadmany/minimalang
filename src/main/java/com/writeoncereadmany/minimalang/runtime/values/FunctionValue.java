package com.writeoncereadmany.minimalang.runtime.values;

import java.util.List;

/**
 * Created by tomj on 22/08/2017.
 */
public interface FunctionValue extends Value {

    Value invoke(List<Value> values);
}