package com.writeoncereadmany.minimalang.runtime.values;

import static java.lang.Double.parseDouble;

/**
 * Created by tomj on 22/08/2017.
 */
public class NumberValue implements Value {

    public final double number;

    public NumberValue(String number) {
        this.number = parseDouble(number);
    }

    @Override
    public String show() {
        return Double.toString(number);
    }
}
