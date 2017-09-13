package com.writeoncereadmany.minimalang.typechecking.types;

import co.unruly.control.result.Result;
import com.writeoncereadmany.minimalang.typechecking.Type;
import com.writeoncereadmany.minimalang.typechecking.TypeError;
import com.writeoncereadmany.minimalang.typechecking.Types;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static co.unruly.control.ApplicableWrapper.startWith;
import static co.unruly.control.result.Introducers.fromMap;
import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public class DataType implements InterfaceType {

    public final String name;
    public final Map<String, Type> fields;

    public DataType(String name, Map<String, Type> fields) {
        this.name = name;
        this.fields = fields;
    }

    @Override
    public Result<Type, List<TypeError>> getField(String name) {
        return startWith(name)
                .then(fromMap(fields, field -> singletonList(new TypeError(format("Type %s has no such field %s", this.name, field)))));
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
    public List<TypeError> assign(Type other, Types types) {
        return types.resolve(other).either(
            otherType -> this.equals(otherType) ? emptyList() : singletonList(new TypeError(format("Cannot assign %s to %s", otherType, this))),
            typeErrors -> typeErrors
        );
    }
}
