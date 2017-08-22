package com.writeoncereadmany.minstrel;

import com.writeoncereadmany.minstrel.ast.Program;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class HelloWorldTest {

    public Compiler compiler = new Compiler();

    @Test
    public void helloWorldPrints() {
        Program program = compiler.compile("print[\"Hello World!\"]");
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
