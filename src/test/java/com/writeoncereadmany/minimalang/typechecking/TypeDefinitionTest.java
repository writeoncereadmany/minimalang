package com.writeoncereadmany.minimalang.typechecking;

import co.unruly.control.pair.Pair;
import co.unruly.control.result.Result;
import com.writeoncereadmany.minimalang.MinimaCompiler;
import com.writeoncereadmany.minimalang.ast.Expression;
import com.writeoncereadmany.minimalang.ast.Program;
import com.writeoncereadmany.minimalang.typechecking.types.NamedType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static co.unruly.control.matchers.ResultMatchers.isFailureOf;
import static co.unruly.control.matchers.ResultMatchers.isSuccessOf;
import static java.lang.String.join;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertThat;

public class TypeDefinitionTest {

    private final NamedType number = new NamedType("Number");
    private final NamedType string = new NamedType("String");
    private final NamedType successType = new NamedType("Success");

    public MinimaCompiler compiler = new MinimaCompiler();

    private final Pair<Types, Expression.Catamorphism<Result<Type, List<TypeError>>, Types>> typeSystem = Typesets.justBuiltins();

    private final Types types = typeSystem.left;
    private final Expression.Catamorphism<Result<Type, List<TypeError>>, Types> typeChecker = typeSystem.right;

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
    public void shouldHandleFunctionAndInterfaceTypeDeclarationsWhereEverythingMatches() {
        Program program = compiler.compile(String.join("\n",
                "type BinaryNumberOperation is [Number, Number] => Number, ",
                "type Monoid is { id : Number, append : BinaryNumberOperation }, ",
                "@Monoid addition is { id: 0, append: [@Number x, @Number y] => x:plus[y] }, ",
                "@Number sum is addition:append[2, 4],",
                "sum"
        ));

        Result<Type, List<TypeError>> result = program.run(typeChecker, types).left;

        assertThat(result, isSuccessOf(number));
    }

    public List<TypeError> singleError(String reason) {
        return asList(new TypeError(reason));
    }

    public List<TypeError> errors(String... reason) {
        return Stream.of(reason).map(TypeError::new).collect(toList());
    }

}
