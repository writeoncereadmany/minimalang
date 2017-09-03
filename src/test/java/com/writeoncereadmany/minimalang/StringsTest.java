package com.writeoncereadmany.minimalang;

import com.writeoncereadmany.minimalang.ast.Program;
import com.writeoncereadmany.minimalang.runtime.Environment;
import com.writeoncereadmany.minimalang.runtime.values.prelude.PrintFunction;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.writeoncereadmany.minimalang.runtime.Evaluator.evaluator;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertThat;

public class StringsTest {

    public Compiler compiler = new Compiler();

    private final List<String> printed = new ArrayList<>();
    private final Environment builtins = new Environment()
        .with("print", new PrintFunction(printed::add));

    @Test
    public void canConcatenateStrings() throws Exception {
        Program program = compiler.compile("print[\"Hello, \":concat[\"World!\"]]");
        program.run(evaluator(), builtins);

        assertThat(printed, hasItems("Hello, World!"));
    }

    @Test
    public void canShowAsMuchAsWeWant() throws Exception {
        Program program = compiler.compile("print[\"Hey\":show[]:show[]:show[]]");
        program.run(evaluator(), builtins);

        assertThat(printed, hasItems("Hey"));
    }

    @Test
    public void canShowNumbers() throws Exception {
        Program program = compiler.compile("print[12.4:show[]:show[]:show[]]");
        program.run(evaluator(), builtins);

        assertThat(printed, hasItems("12.4"));
    }
}
