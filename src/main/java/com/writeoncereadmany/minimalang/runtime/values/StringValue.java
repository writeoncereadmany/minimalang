package com.writeoncereadmany.minimalang.runtime.values;

import com.writeoncereadmany.minimalang.runtime.values.prelude.BuiltinFunction;
import com.writeoncereadmany.minimalang.runtime.values.prelude.Extractors;

import static co.unruly.control.pair.Maps.entry;
import static co.unruly.control.pair.Maps.mapOf;

/**
 * Created by tomj on 22/08/2017.
 */
public class StringValue extends ObjectValue {

    public final String text;

    public StringValue(String text) {
        super(mapOf(
            entry("concat",
                new BuiltinFunction<>(
                    "String:concat[]",
                    Extractors.singleParamOfType(StringValue.class),
                    (string) -> new StringValue(text.concat(string.text)))),
            entry("show",
                new BuiltinFunction<>(
                    "String:show[]",
                    Extractors.noParams(),
                    __ -> new StringValue(text)
                ))));
        this.text = text;
    }

    @Override
    public String show() {
        return text;
    }
}
