package com.writeoncereadmany.minimalang.ast;

import java.util.List;

/**
 * Represents a (possibly-annotated) identifier, such as a variable,
 * parameter, or field
 */
public class Introduction {

    public final List<String> annotations;
    public final String name;

    public Introduction(List<String> annotations, String name) {
        this.annotations = annotations;
        this.name = name;
    }
}
