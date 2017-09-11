package com.writeoncereadmany.minimalang;

import com.writeoncereadmany.minimalang.ast.Program;
import com.writeoncereadmany.minimalang.runtime.Environment;
import com.writeoncereadmany.minimalang.runtime.values.prelude.PrintFunction;
import com.writeoncereadmany.minimalang.runtime.values.prelude.SuccessValue;
import com.writeoncereadmany.minimalang.support.Loader;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.writeoncereadmany.minimalang.runtime.Evaluator.evaluator;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertThat;

public class FizzBuzzTest {

    public Compiler compiler = new Compiler();

    private final List<String> printed = new ArrayList<>();
    private final Environment builtins = new Environment()
        .with("print", new PrintFunction(printed::add))
        .with("SUCCESS", SuccessValue.SUCCESS);

    @Test
    public void canIterateOverAList() throws Exception {
        String sourceCode = Loader.loadSource("fizzbuzz.mml");

        Program program = compiler.compile(sourceCode);
        program.run(evaluator(), builtins);

        assertThat(printed, contains(
            "1.0",
            "2.0",
            "fizz",
            "4.0",
            "buzz",
            "fizz",
            "7.0",
            "8.0",
            "fizz",
            "buzz",
            "11.0",
            "fizz",
            "13.0",
            "14.0",
            "fizzbuzz",
            "16.0",
            "17.0",
            "fizz",
            "19.0",
            "buzz"
        ));
    }
}
