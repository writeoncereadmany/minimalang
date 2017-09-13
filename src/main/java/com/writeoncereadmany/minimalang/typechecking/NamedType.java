package com.writeoncereadmany.minimalang.typechecking;

import java.util.Objects;

public class NamedType implements Type {

    public final String name;

    public NamedType(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NamedType namedType = (NamedType) o;
        return Objects.equals(name, namedType.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "NamedType{" +
                "name='" + name + '\'' +
                '}';
    }
}
