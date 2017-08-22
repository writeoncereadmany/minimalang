package com.writeoncereadmany.minimalang.runtime.values.prelude;

import com.writeoncereadmany.minimalang.runtime.MinimalangExecutionException;
import com.writeoncereadmany.minimalang.runtime.values.FunctionValue;
import com.writeoncereadmany.minimalang.runtime.values.StringValue;
import com.writeoncereadmany.minimalang.runtime.values.Value;

import java.util.List;
import java.util.function.Consumer;

import static com.writeoncereadmany.minimalang.runtime.values.prelude.SuccessValue.SUCCESS;

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
    public Value invoke(List<Value> values) {
        if(values.size() != 1) {
            throw new MinimalangExecutionException("Arity error");
        }
        Value firstParam = values.get(0);
        if(!(firstParam instanceof StringValue)) {
            throw new MinimalangExecutionException("Type error: String expected");
        }
        printChannel.accept(((StringValue)firstParam).text);

        return SUCCESS;
    }
}
