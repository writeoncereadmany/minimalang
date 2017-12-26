package com.writeoncereadmany.minimalang.typechecking;

import java.util.List;

import static java.util.stream.Collectors.joining;

public class TypecheckFailed extends RuntimeException {

    public TypecheckFailed(List<TypeError> typeErrors) {
        this(typeErrors
            .stream()
            .map(TypeError::reason)
            .collect(joining("\n")));
    }

    public TypecheckFailed(String msg) {
        super(msg);
    }
}
