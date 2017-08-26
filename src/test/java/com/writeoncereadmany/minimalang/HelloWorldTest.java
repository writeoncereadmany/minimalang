package com.writeoncereadmany.minimalang;

import com.writeoncereadmany.minimalang.ast.Program;
import com.writeoncereadmany.minimalang.runtime.values.Value;
import com.writeoncereadmany.minimalang.runtime.values.prelude.PrintFunction;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static co.unruly.control.pair.Maps.entry;
import static co.unruly.control.pair.Maps.mapOf;
import static com.writeoncereadmany.minimalang.runtime.Evaluator.evaluator;
import static com.writeoncereadmany.minimalang.runtime.values.prelude.SuccessValue.SUCCESS;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HelloWorldTest {

    public Compiler compiler = new Compiler();

    private final List<String> printed = new ArrayList<String>();
    private final Map<String, Value> prelude = mapOf(entry("print", new PrintFunction(printed::add)));

    @Test
    public void helloWorldPrints() {
        Program program = compiler.compile("print['Hello World!']");

        Value endResult = program.run(evaluator(), prelude);

        assertThat(endResult, is(SUCCESS));
        assertThat(printed, hasItems("Hello World!"));
    }

    @Test
    public void canPrintMultipleThings() {
        Program program = compiler.compile(String.join("\n",
                "print['Seven syllables to start']",
                "print['Then another five']",
                "print['And seven more to finish']"));

        Value endResult = program.run(evaluator(), prelude);

        assertThat(endResult, is(SUCCESS));
        assertThat(printed, hasItems("Seven syllables to start", "Then another five", "And seven more to finish"));
    }

    @Test
    public void canGroupMultipleThingsWithParentheses() {
        Program program = compiler.compile(String.join("\n",
                "( print['Seven syllables to start'], ",
                "print['Then another five'], ",
                "print['And seven more to finish'])"));

        Value endResult = program.run(evaluator(), prelude);

        assertThat(endResult, is(SUCCESS));
        assertThat(printed, hasItems("Seven syllables to start", "Then another five", "And seven more to finish"));
    }

    @Test
    public void canCreateAndAccessObjects() {
        Program program = compiler.compile(String.join("\n",
                "point is { x: 'Hello', y: 'World' }",
                "print[point.x]",
                "print[point.y]"
            ));

        Value endResult = program.run(evaluator(), prelude);

        assertThat(endResult, is(SUCCESS));
        assertThat(printed, hasItems("Hello", "World"));
    }

    @Test
    public void canCreateCustomFunctions() {
        Program program = compiler.compile(String.join("\n",
            "first is [x, y] => x",
            "print[first['Hello', 'World']]"
        ));

        Value endResult = program.run(evaluator(), prelude);

        assertThat(endResult, is(SUCCESS));
        assertThat(printed, hasItems("Hello"));
    }

    @Test
    public void canStoreThingsInVariables() {
        Program program = compiler.compile(String.join("\n",
                "message is 'Hello, World!'",
                "print[message]",
                "print[message]"
        ));

        Value endResult = program.run(evaluator(), prelude);

        assertThat(endResult, is(SUCCESS));
        assertThat(printed, hasItems("Hello, World!", "Hello, World!"));
    }

    @Test
    public void failsToParseUnknownSymbols() {
        assertThrows(LexException.class, () -> compiler.compile("!£#@<do24"));
    }
}
