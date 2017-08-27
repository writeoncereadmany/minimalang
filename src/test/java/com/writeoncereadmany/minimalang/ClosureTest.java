package com.writeoncereadmany.minimalang;

import com.writeoncereadmany.minimalang.ast.Program;
import com.writeoncereadmany.minimalang.runtime.Evaluator;
import com.writeoncereadmany.minimalang.runtime.values.Value;
import com.writeoncereadmany.minimalang.runtime.values.prelude.PrintFunction;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static co.unruly.control.pair.Maps.entry;
import static co.unruly.control.pair.Maps.mapOf;
import static com.writeoncereadmany.minimalang.runtime.Evaluator.evaluator;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertThat;

public class ClosureTest {

    public Compiler compiler = new Compiler();

    private final List<String> printed = new ArrayList<>();
    private final Map<String, Value> builtins = mapOf(entry("print", new PrintFunction(printed::add)));

    @Test
    public void canBindOverAnEnvironment() throws Exception {
        String sourceCode = String.join("\n", Files.readAllLines(Paths.get("src/test/resources/closure.mml")));

        Program program = compiler.compile(sourceCode);
        program.run(evaluator(), builtins);

        assertThat(printed, hasItems("Hello!", "World!"));
    }
}
