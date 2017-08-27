package com.writeoncereadmany.minimalang;

import com.writeoncereadmany.minimalang.ast.Program;
import com.writeoncereadmany.minimalang.runtime.Environment;
import com.writeoncereadmany.minimalang.runtime.values.prelude.PrintFunction;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static co.unruly.control.pair.Maps.entry;
import static co.unruly.control.pair.Maps.mapOf;
import static com.writeoncereadmany.minimalang.runtime.Evaluator.evaluator;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertThat;

public class MathsTest {

    public Compiler compiler = new Compiler();

    private final List<String> printed = new ArrayList<>();
    private final Environment builtins = new Environment().with("print", new PrintFunction(printed::add));

    @Test
    public void canAddNumbers() throws Exception {
        Program program = compiler.compile("print[2:plus[4.5]]");
        program.run(evaluator(), builtins);

        assertThat(printed, hasItems("6.5"));
    }

    @Test
    public void canSubtractNumbers() throws Exception {
        Program program = compiler.compile("print[2:minus[1.5]]");
        program.run(evaluator(), builtins);

        assertThat(printed, hasItems("0.5"));
    }

    @Test
    public void canMultiplyNumbers() throws Exception {
        Program program = compiler.compile("print[12.2:multiplyBy[4.5]]");
        program.run(evaluator(), builtins);

        assertThat(printed, hasItems("54.9"));
    }

    @Test
    public void canDivideNumbers() throws Exception {
        Program program = compiler.compile("print[12:divideBy[2.5]]");
        program.run(evaluator(), builtins);

        assertThat(printed, hasItems("4.8"));
    }
}
