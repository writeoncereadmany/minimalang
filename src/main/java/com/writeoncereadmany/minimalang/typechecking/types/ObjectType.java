package com.writeoncereadmany.minimalang.typechecking.types;

import co.unruly.control.result.Result;
import com.writeoncereadmany.minimalang.typechecking.Type;
import com.writeoncereadmany.minimalang.typechecking.TypeError;
import com.writeoncereadmany.minimalang.typechecking.Types;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static co.unruly.control.ApplicableWrapper.startWith;
import static co.unruly.control.result.Introducers.fromMap;
import static co.unruly.control.result.Introducers.ifType;
import static co.unruly.control.result.Match.matchValue;
import static co.unruly.control.result.Resolvers.collapse;
import static co.unruly.control.result.Transformers.onSuccess;
import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

public class ObjectType implements InterfaceType {

    private final Map<String, Type> fields;

    public ObjectType(Map<String, Type> fields) {
        this.fields = fields;
    }

    @Override
    public Result<Type, List<TypeError>> getField(String name) {
        return startWith(name)
            .then(fromMap(
                    fields,
                    field -> singletonList(new TypeError(format("Object has no such field %s", field)))
            ));
    }

    @Override
    public List<TypeError> assign(Type other, Types types) {
        return types.resolve(other).either(
            otherType -> matchValue(otherType,
                    ifType(InterfaceType.class, obj -> assign(obj, types))
            ).otherwise(fun -> singletonList(new TypeError("Cannot assign function types to an object type"))),
            typeErrors -> typeErrors
        );
    }

    private List<TypeError> assign(InterfaceType interfaceType, Types types) {
        return fields.entrySet()
                .stream()
                .map(entry -> interfaceType
                        .getField(entry.getKey())
                        .then(onSuccess(success -> entry.getValue().assign(success, types))))
                .map(collapse())
                .flatMap(List::stream)
                .collect(toList());
    }
}
