package com.writeoncereadmany.minimalang;

import com.writeoncereadmany.minimalang.ast.Program;
import com.writeoncereadmany.minimalang.runtime.Environment;
import com.writeoncereadmany.minimalang.runtime.values.prelude.PrintFunction;
import com.writeoncereadmany.minimalang.runtime.values.prelude.SuccessValue;
import com.writeoncereadmany.minimalang.support.Loader;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.writeoncereadmany.minimalang.runtime.Evaluator.evaluator;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

public class PeanoTest {

    public MinimaCompiler compiler = new MinimaCompiler();

    private final List<String> printed = new ArrayList<>();
    private final Environment builtins = new Environment()
        .with("print", new PrintFunction(printed::add))
        .with("SUCCESS", SuccessValue.SUCCESS);

    @Test
    public void canOperateOnNumbers() throws Exception {
        String sourceCode = Loader.loadSource("peano.mml");

        Program program = compiler.compile(sourceCode);
        program.run(evaluator(), builtins);

        assertThat(printed, contains("!!!"));
    }
}
