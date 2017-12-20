package com.writeoncereadmany.minimalang.typechecking;

import com.writeoncereadmany.minimalang.MinimaCompiler;
import com.writeoncereadmany.minimalang.ast.Program;
import com.writeoncereadmany.minimalang.runtime.Environment;
import com.writeoncereadmany.minimalang.runtime.values.prelude.PrintFunction;
import com.writeoncereadmany.minimalang.runtime.values.prelude.SuccessValue;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class TypeDefinitionTest {

    public MinimaCompiler compiler = new MinimaCompiler();

    private final List<String> printed = new ArrayList<>();
    private final Environment builtins = new Environment()
        .with("print", new PrintFunction(printed::add))
        .with("SUCCESS", SuccessValue.SUCCESS);

    @Test
    public void canParseTypeAlias() {
        Program program = compiler.compile("type Text is String");
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
}
