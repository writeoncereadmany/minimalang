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

    @Test
    public void helloWorldPrints() {
        List<String> printed = new ArrayList<String>();

        Map<String, Value> environment = mapOf(entry("print", new PrintFunction(printed::add)));

        Program program = compiler.compile("print[\"Hello World!\"]");

        Value endResult = program.run(evaluator(environment));

        assertThat(endResult, is(SUCCESS));
        assertThat(printed, hasItems("Hello World!"));
    }

    @Test
    public void canPrintMultipleThings() {
        List<String> printed = new ArrayList<String>();

        Map<String, Value> environment = mapOf(entry("print", new PrintFunction(printed::add)));

        Program program = compiler.compile(String.join("\n",
                "print[\"Seven syllables begin\"]",
                "print[\"Then another five\"]",
                "print[\"And seven more to finish\"]"));

        Value endResult = program.run(evaluator(environment));

        assertThat(endResult, is(SUCCESS));
        assertThat(printed, hasItems("Seven syllables begin", "Then another five", "And seven more to finish"));
    }

    @Test
    public void failsToParseBareWords() {
        assertThrows(ParseException.class, () -> compiler.compile("Hello World"));
    }

    @Test
    public void failsToParseUnknownSymbols() {
        assertThrows(LexException.class, () -> compiler.compile("!£#@<do24"));
    }
}
