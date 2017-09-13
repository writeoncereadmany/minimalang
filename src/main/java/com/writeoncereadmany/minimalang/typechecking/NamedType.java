package com.writeoncereadmany.minimalang.typechecking;

public class NamedType implements Type {

    public final String name;

    public NamedType(String name) {
        this.name = name;
    }
}
