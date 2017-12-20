package com.writeoncereadmany.minimalang.typechecking;

import co.unruly.control.pair.Pair;
import co.unruly.control.result.Result;
import com.writeoncereadmany.minimalang.MinimaCompiler;
import com.writeoncereadmany.minimalang.ast.Program;
import com.writeoncereadmany.minimalang.ast.expressions.Expression;
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
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertThat;

public class TypeCheckerTest {

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
            .withNamedType("Number", numberImpl)
            .withNamedType("String", stringImpl)
            .withNamedType("Success", successImpl)
            .withNamedType("Showable", showableImpl);

    @Test
    public void canAssignStringToStringVariable() {
        Program program = compiler.compile("@String name is \"Tom\"");
        Pair<Result<Type, List<TypeError>>, Types> result = program.run(typeChecker, types);

        assertThat(result.left, isSuccessOf(successType));
    }

    @Test
    public void cannotAssignNumberToStringVariable() {
        Program program = compiler.compile("@String name is 42");
        Pair<Result<Type, List<TypeError>>, Types> result = program.run(typeChecker, types);

        assertThat(result.left, isFailureOf(singleError("Cannot assign DataType{name='Number'} to DataType{name='String'}")));
    }

    @Test
    public void canAddTwoNumbers() {
        Program program = compiler.compile("2:plus[4]");
        Pair<Result<Type, List<TypeError>>, Types> result = program.run(typeChecker, types);

        assertThat(result.left, isSuccessOf(number));
    }

    @Test
    public void canAddThreeNumbers() {
        Program program = compiler.compile("2:plus[4]:plus[6]");
        Pair<Result<Type, List<TypeError>>, Types> result = program.run(typeChecker, types);

        assertThat(result.left, isSuccessOf(number));
    }

    @Test
    public void cannotAddStringToNumber() {
        Program program = compiler.compile("2:plus[\"Hello, World!\"]");
        Pair<Result<Type, List<TypeError>>, Types> result = program.run(typeChecker, types);

        assertThat(result.left, isFailureOf(singleError(
            "Cannot assign DataType{name='String'} to DataType{name='Number'}"
        )));
    }

    @Test
    public void canConcatenateTwoStrings() {
        Program program = compiler.compile("\"Hello, \":concat[\"World!\"]");
        Pair<Result<Type, List<TypeError>>, Types> result = program.run(typeChecker, types);

        assertThat(result.left, isSuccessOf(string));
    }

    @Test
    public void canConcatenateThreeStrings() {
        Program program = compiler.compile("\"Yabba, \":concat[\"Dabba, \"]:concat[\"Doo!\"]");
        Pair<Result<Type, List<TypeError>>, Types> result = program.run(typeChecker, types);

        assertThat(result.left, isSuccessOf(string));
    }

    @Test
    public void canDefineObjectsWithAnnotatedTypesWhenTypesMatch() {
        Program program = compiler.compile("point is { @Number x : 2, @Number y : 3 }");
        Pair<Result<Type, List<TypeError>>, Types> result = program.run(typeChecker, types);

        assertThat(result.left, isSuccessOf(successType));
    }

    @Test
    public void cannotDefineObjectsWithAnnotatedTypesWhenTypesDoNotMatch() {
        Program program = compiler.compile("point is { @String x : 2, @Success y : 3 }");
        Pair<Result<Type, List<TypeError>>, Types> result = program.run(typeChecker, types);

        assertThat(result.left, isFailureOf(errors(
            "Cannot assign DataType{name='Number'} to DataType{name='String'}",
            "Cannot assign DataType{name='Number'} to DataType{name='Success'}"
        )));
    }

    @Test
    public void canAddFieldsOfAnObjectWhichAreNumbers() {
        Program program = compiler.compile(String.join("\n",
                "point is { x : 2, y : 3 }",
                "point:x:plus[point:y]"));
        Pair<Result<Type, List<TypeError>>, Types> result = program.run(typeChecker, types);

        assertThat(result.left, isSuccessOf(number));
    }

    @Test
    public void cannotAddFieldsOfAnObjectWhichAreStrings() {
        Program program = compiler.compile(String.join("\n",
                "point is { x : \"Hello\", y : \"World\" }",
                "point:x:plus[point:y]"));
        Pair<Result<Type, List<TypeError>>, Types> result = program.run(typeChecker, types);

        assertThat(result.left, isFailureOf(singleError("Type String has no such field plus")));
    }

    @Test
    public void supportsPolymorphicFunctionTakingString() {
        Program program = compiler.compile("print[\"Hello, World!\"]");
        Pair<Result<Type, List<TypeError>>, Types> result = program.run(typeChecker, types);

        assertThat(result.left, isSuccessOf(successType));
    }

    @Test
    public void supportsPolymorphicFunctionTakingNumber() {
        Program program = compiler.compile("print[12]");
        Pair<Result<Type, List<TypeError>>, Types> result = program.run(typeChecker, types);

        assertThat(result.left, isSuccessOf(successType));
    }

    @Test
    public void rejectsPolymorphicFunctionTakingObjectWithoutShow() {
        Program program = compiler.compile("print[{ x: 12, y: 14 }]");
        Pair<Result<Type, List<TypeError>>, Types> result = program.run(typeChecker, types);

        assertThat(result.left, isFailureOf(singleError("Object has no such field show")));
    }

    @Test
    public void rejectsPolymorphicFunctionTakingObjectWithNonFunctionShow() {
        Program program = compiler.compile("print[{ show: \"Cheese!\"}]");
        Pair<Result<Type, List<TypeError>>, Types> result = program.run(typeChecker, types);

        assertThat(result.left, isFailureOf(singleError("Cannot assign an object type to a function type")));
    }

    public List<TypeError> singleError(String reason) {
        return asList(new TypeError(reason));
    }

    public List<TypeError> errors(String... reason) {
        return Stream.of(reason).map(TypeError::new).collect(toList());
    }
}
