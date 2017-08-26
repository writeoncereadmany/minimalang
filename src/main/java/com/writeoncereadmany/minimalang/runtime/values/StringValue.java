package com.writeoncereadmany.minimalang.runtime.values;

/**
 * Created by tomj on 22/08/2017.
 */
public class StringValue implements Value {

    public final String text;

    public StringValue(String text) {
        this.text = text;
    }

    @Override
    public String show() {
        return text;
    }
}
