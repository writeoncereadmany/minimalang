package com.writeoncereadmany.minimalang.typechecking;

import co.unruly.control.result.Result;

import java.util.Map;

import static co.unruly.control.ApplicableWrapper.startWith;
import static co.unruly.control.result.Introducers.fromMap;
import static co.unruly.control.result.Introducers.ifType;
import static co.unruly.control.result.Match.matchValue;
import static co.unruly.control.result.Transformers.attempt;
import static com.writeoncereadmany.minimalang.util.MapUtils.immutablePut;
import static java.lang.String.format;

public class Types {

    private final Map<String, Type> variables;
    private final Map<String, Type> namedTypes;

    public Types(Map<String, Type> variables, Map<String, Type> namedTypes) {
        this.variables = variables;
        this.namedTypes = namedTypes;
    }

    public Result<Type, TypeError> typeOf(String name) {
        return startWith(name)
                .then(fromMap(variables, n -> new TypeError(format("Variable %s not found", n))));
    }

    public Result<Type, TypeError> typeNamed(String name) {
        return startWith(name)
                .then(fromMap(namedTypes, n -> new TypeError(format("Type %s not defined", n))));
    }

    public Result<Type, TypeError> resolve(Type type) {
        return matchValue(type,
            ifType(NamedType.class, nt -> typeNamed(nt.name).then(attempt(this::resolve)))
        ).otherwise(Result::success);
    }

    public Types withVariable(String name, Type type) {
        return new Types(immutablePut(variables, name, type), namedTypes);
    }
}
