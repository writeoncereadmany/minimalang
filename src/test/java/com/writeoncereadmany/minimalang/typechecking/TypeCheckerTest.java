package com.writeoncereadmany.minimalang.typechecking;

import co.unruly.control.pair.Pair;
import co.unruly.control.result.Result;
import com.writeoncereadmany.minimalang.MinimaCompiler;
import com.writeoncereadmany.minimalang.ast.Program;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static co.unruly.control.matchers.ResultMatchers.isSuccessOf;
import static org.junit.Assert.assertThat;

public class TypeCheckerTest {

    public MinimaCompiler compiler = new MinimaCompiler();

    @Test
    @Disabled
    public void canAddTwoNumbers() {
        Program program = compiler.compile("2:plus[4]");
        Pair<Result<Type, TypeError>, Types> result = program.run(
            TypeChecker.typeChecker(
                new NamedType("Number"),
                new NamedType("String"),
                new NamedType("Success"))
            , new Types().withNamedType("Number", new DataType("Number")));

        assertThat(result.left, isSuccessOf(new NamedType("Number")));
    }
}
