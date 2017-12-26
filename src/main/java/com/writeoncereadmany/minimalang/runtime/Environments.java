package com.writeoncereadmany.minimalang.runtime;

import com.writeoncereadmany.minimalang.runtime.values.prelude.PrintFunction;

import java.util.function.Consumer;

public class Environments {

    public static Environment justPrint(Consumer<String> printer) {
        return new Environment().with("print", new PrintFunction(printer));
    }
}
