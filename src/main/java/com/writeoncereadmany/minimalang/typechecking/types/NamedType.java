package com.writeoncereadmany.minimalang.typechecking.types;

import com.writeoncereadmany.minimalang.typechecking.Type;
import com.writeoncereadmany.minimalang.typechecking.TypeError;
import com.writeoncereadmany.minimalang.typechecking.Types;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static co.unruly.control.result.Resolvers.collapse;
import static co.unruly.control.result.Transformers.onSuccess;

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

    @Override
    public List<TypeError> assign(Type other, Types types) {
        // this is a little dangerous until we use debruijn indices - at least, if we allow introduction of new names
        if(this.equals(other)) {
            return Collections.emptyList();
        }
        return types.resolve(this)
                .then(onSuccess(resolvedType -> resolvedType.assign(other, types)))
                .then(collapse());
    }
}
