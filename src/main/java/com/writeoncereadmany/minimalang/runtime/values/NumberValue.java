package com.writeoncereadmany.minimalang.runtime.values;

import com.writeoncereadmany.minimalang.runtime.values.prelude.BuiltinFunction;
import com.writeoncereadmany.minimalang.runtime.values.prelude.Extractors;

import java.util.function.BiFunction;

import static co.unruly.control.pair.Maps.entry;
import static co.unruly.control.pair.Maps.mapOf;
import static com.writeoncereadmany.minimalang.runtime.values.prelude.BooleanFunctions.FALSE;
import static com.writeoncereadmany.minimalang.runtime.values.prelude.BooleanFunctions.TRUE;
import static com.writeoncereadmany.minimalang.runtime.values.prelude.Extractors.singleParamOfType;
import static java.lang.Double.parseDouble;

/**
 * Created by tomj on 22/08/2017.
 */
public class NumberValue extends ObjectValue {

    private final double number;

    public NumberValue(String number) {
        this(parseDouble(number));
    }

    public NumberValue(double number) {
        super(mapOf(
            entry("plus", binaryOperator("plus", number, (a, b) -> new NumberValue(a + b))),
            entry("minus", binaryOperator("minus", number, (a, b) -> new NumberValue(a - b))),
            entry("multiplyBy", binaryOperator("multiplyBy", number, (a, b) -> new NumberValue(a * b))),
            entry("divideBy", binaryOperator("divideBy", number, (a, b) -> new NumberValue(a / b))),
            entry("lessThan", binaryOperator("lessThan", number, (a, b) -> a < b ? TRUE : FALSE)),
            entry("divisibleBy", binaryOperator("divisibleBy", number, (a, b) -> a % b == 0 ? TRUE : FALSE)),
            entry("show", new BuiltinFunction<>(
                "Number:show[]",
                Extractors.noParams(),
                __ -> new StringValue(Double.toString(number))
            ))
        ));

        this.number = number;
    }

    @Override
    public String show() {
        return Double.toString(number);
    }

    private static Value binaryOperator(String name, double value, BiFunction<Double, Double, Value> operation) {
        return new BuiltinFunction<>(
            String.format("Number:%s[]", name),
            singleParamOfType(NumberValue.class),
            nv -> operation.apply(value, nv.number));
    }
}
