package com.writeoncereadmany.minimalang.typechecking;

import co.unruly.control.result.Result;
import com.writeoncereadmany.minimalang.MinimaCompiler;
import com.writeoncereadmany.minimalang.ast.Expression;
import com.writeoncereadmany.minimalang.ast.Program;
import com.writeoncereadmany.minimalang.typechecking.types.ConcreteFunctionType;
import com.writeoncereadmany.minimalang.typechecking.types.DataType;
import com.writeoncereadmany.minimalang.typechecking.types.NamedType;
import com.writeoncereadmany.minimalang.typechecking.types.ObjectType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static co.unruly.control.matchers.ResultMatchers.isFailureOf;
import static co.unruly.control.matchers.ResultMatchers.isSuccessOf;
import static co.unruly.control.pair.Maps.entry;
import static co.unruly.control.pair.Maps.mapOf;
import static java.lang.String.join;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertThat;

public class TypeDefinitionTest {

    public MinimaCompiler compiler = new MinimaCompiler();
    private final NamedType number = new NamedType("Number");
    private final NamedType string = new NamedType("String");
    private final NamedType successType = new NamedType("Success");
    private final NamedType showable = new NamedType("Showable");

    private final Type numberImpl = new DataType("Number", mapOf(
        entry("plus", new ConcreteFunctionType(asList(number), number)),
        entry("show", new ConcreteFunctionType(emptyList(), string))
    ));

    private final Type stringImpl = new DataType("String", mapOf(
        entry("concat", new ConcreteFunctionType(asList(string), string)),
        entry("show", new ConcreteFunctionType(emptyList(), string))
    ));

    private final Type successImpl = new DataType("Success", emptyMap());

    private final Type showableImpl = new ObjectType(mapOf(entry("show", new ConcreteFunctionType(emptyList(), string))));
    private final Type printType = new ConcreteFunctionType(asList(showable), successType);

    private final Expression.Catamorphism<Result<Type, List<TypeError>>, Types> typeChecker = TypeChecker.typeChecker(
        number,
        string,
        successType);

    private final Types types = new Types()
        .withVariable("print", printType)
        .withVariable("Success", successImpl)
        .withNamedType("Number", numberImpl)
        .withNamedType("String", stringImpl)
        .withNamedType("Success", successImpl)
        .withNamedType("Showable", showableImpl);

    @Test
    public void typeChecksWhenAliasedTypesMatch() {
        Program program = compiler.compile(join("\n",
            "type Text is String,",
            "@Text greeting is \"Hello!\""));

        Result<Type, List<TypeError>> result = program.run(typeChecker, types).left;

        assertThat(result, isSuccessOf(successType));
    }

    @Test
    public void typecheckFailsWhenAliasedTypesMismatch() {
        Program program = compiler.compile(join("\n",
            "type Text is String,",
            "@Text greeting is 42"));

        Result<Type, List<TypeError>> result = program.run(typeChecker, types).left;

        assertThat(result, isFailureOf(singleError("Cannot assign DataType{name='Number'} to DataType{name='String'}")));
    }

    @Test
    public void canParseFunctionType() {
        Program program = compiler.compile("type BinaryNumberOperation is [Number, Number] => Number");
    }

    @Test
    public void canParseInterfaceType() {
        Program program = compiler.compile("type Point is { x : Number, y : Number}");
    }

    @Test
    public void canParseNestedTypeDefinitions() {
        Program program = compiler.compile("type Monoid is { id : Number, append : [Number, Number] => Number }");

        System.out.println(program);
    }

    public List<TypeError> singleError(String reason) {
        return asList(new TypeError(reason));
    }

    public List<TypeError> errors(String... reason) {
        return Stream.of(reason).map(TypeError::new).collect(toList());
    }

}
