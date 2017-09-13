package com.writeoncereadmany.minimalang.typechecking;

import co.unruly.control.pair.Pair;
import co.unruly.control.result.Result;
import com.writeoncereadmany.minimalang.MinimaCompiler;
import com.writeoncereadmany.minimalang.ast.Program;
import com.writeoncereadmany.minimalang.ast.expressions.Expression;
import com.writeoncereadmany.minimalang.typechecking.types.ConcreteFunctionType;
import com.writeoncereadmany.minimalang.typechecking.types.DataType;
import com.writeoncereadmany.minimalang.typechecking.types.NamedType;
import org.junit.jupiter.api.Test;

import static co.unruly.control.matchers.ResultMatchers.isFailureOf;
import static co.unruly.control.matchers.ResultMatchers.isSuccessOf;
import static co.unruly.control.pair.Maps.entry;
import static co.unruly.control.pair.Maps.mapOf;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertThat;

public class TypeCheckerTest {

    public MinimaCompiler compiler = new MinimaCompiler();

    private final NamedType number = new NamedType("Number");
    private final NamedType string = new NamedType("String");
    private final NamedType successType = new NamedType("Success");

    private final DataType numberImpl = new DataType("Number", mapOf(entry("plus", new ConcreteFunctionType(asList(number), number))));

    private final DataType stringImpl = new DataType("String", mapOf(entry("concat", new ConcreteFunctionType(asList(string), string))));

    private final Expression.Catamorphism<Result<Type, TypeError>, Types> cata = TypeChecker.typeChecker(
            number,
            string,
            successType);

    private final Types types = new Types()
            .withNamedType("Number", numberImpl)
            .withNamedType("String", stringImpl);

    @Test
    public void canAddTwoNumbers() {
        Program program = compiler.compile("2:plus[4]");
        Pair<Result<Type, TypeError>, Types> result = program.run(cata, types);

        assertThat(result.left, isSuccessOf(number));
    }

    @Test
    public void canAddThreeNumbers() {
        Program program = compiler.compile("2:plus[4]:plus[6]");
        Pair<Result<Type, TypeError>, Types> result = program.run(cata, types);

        assertThat(result.left, isSuccessOf(number));
    }

    @Test
    public void cannotAddStringToNumber() {
        Program program = compiler.compile("2:plus[\"Hello, World!\"]");
        Pair<Result<Type, TypeError>, Types> result = program.run(cata, types);

        assertThat(result.left, isFailureOf(new TypeError("Cannot assign DataType{name='String'} to DataType{name='Number'}")));
    }
}
