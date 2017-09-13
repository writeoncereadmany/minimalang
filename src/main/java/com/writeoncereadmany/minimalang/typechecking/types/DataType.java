package com.writeoncereadmany.minimalang.typechecking.types;

import co.unruly.control.result.Result;
import com.writeoncereadmany.minimalang.typechecking.Type;
import com.writeoncereadmany.minimalang.typechecking.TypeError;
import com.writeoncereadmany.minimalang.typechecking.Types;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static co.unruly.control.ApplicableWrapper.startWith;
import static co.unruly.control.result.Introducers.fromMap;
import static java.lang.String.format;

public class DataType implements InterfaceType {

    public final String name;
    public final Map<String, Type> fields;

    public DataType(String name, Map<String, Type> fields) {
        this.name = name;
        this.fields = fields;
    }

    @Override
    public Result<Type, TypeError> getField(String name) {
        return startWith(name)
                .then(fromMap(fields, field -> new TypeError(format("Type %s has no such field %s", this.name, field))));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataType dataType = (DataType) o;
        return Objects.equals(name, dataType.name) &&
                Objects.equals(fields, dataType.fields);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, fields);
    }

    @Override
    public String toString() {
        return "DataType{" +
                "name='" + name + '\'' +
                '}';
    }

    @Override
    public Optional<TypeError> assign(Type other, Types types) {
        return types.resolve(other).either(
            otherType -> this.equals(otherType) ? Optional.empty() : Optional.of(new TypeError(format("Cannot assign %s to %s", otherType, this))),
            typeError -> Optional.of(typeError)
        );
    }
}
