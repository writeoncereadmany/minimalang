package com.writeoncereadmany.minimalang.typechecking;

import co.unruly.control.pair.Pair;
import co.unruly.control.result.Result;
import com.writeoncereadmany.minimalang.ast.Expression;
import com.writeoncereadmany.minimalang.typechecking.types.ConcreteFunctionType;
import com.writeoncereadmany.minimalang.typechecking.types.DataType;
import com.writeoncereadmany.minimalang.typechecking.types.NamedType;
import com.writeoncereadmany.minimalang.typechecking.types.ObjectType;

import java.util.List;

import static co.unruly.control.pair.Maps.entry;
import static co.unruly.control.pair.Maps.mapOf;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

public class Typesets {

    public static Pair<Types, Expression.Catamorphism<Result<Type, List<TypeError>>, Types>> justBuiltins() {
        final NamedType number = new NamedType("Number");
        final NamedType string = new NamedType("String");
        final NamedType successType = new NamedType("Success");
        final NamedType showable = new NamedType("Showable");

        final Type numberImpl = new DataType("Number", mapOf(
            entry("plus", new ConcreteFunctionType(asList(number), number)),
            entry("show", new ConcreteFunctionType(emptyList(), string))
        ));

        final Type stringImpl = new DataType("String", mapOf(
            entry("concat", new ConcreteFunctionType(asList(string), string)),
            entry("show", new ConcreteFunctionType(emptyList(), string))
        ));

        final Type successImpl = new DataType("Success", emptyMap());

        final Type showableImpl = new ObjectType(mapOf(entry("show", new ConcreteFunctionType(emptyList(), string))));
        final Type printType = new ConcreteFunctionType(asList(showable), successType);

        final Expression.Catamorphism<Result<Type, List<TypeError>>, Types> typeChecker = TypeChecker.typeChecker(
            number,
            string,
            successType);

        Types types = new Types()
            .withVariable("print", printType)
            .withVariable("Success", successImpl)
            .withNamedType("Number", numberImpl)
            .withNamedType("String", stringImpl)
            .withNamedType("Success", successImpl)
            .withNamedType("Showable", showableImpl);
        return Pair.of(types, typeChecker);
    }
}
