package com.writeoncereadmany.minstrel;

import com.writeoncereadmany.minstrel.ast.Program;
import org.junit.jupiter.api.Test;

/**
 * Created by tomj on 27/07/2017.
 */
public class HelloWorldTest {

    public Compiler compiler = new Compiler();

    @Test
    public void helloWorldPrints() {
        Program program = compiler.compile("print[\"Hello World!\"]");
    }

    @Test
    public void failsToParseBareWords() {
        Program program = compiler.compile("Hello World");
    }

    @Test
    public void failsToParseUnknownSymbols() {
        Program program = compiler.compile("!£#@<do24");
    }
}
