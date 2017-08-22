package com.writeoncereadmany.minimalang;

import com.writeoncereadmany.minimalang.ast.Program;
import com.writeoncereadmany.minimalang.ast.ProgramBuilder;
import com.writeoncereadmany.minimalang.generated.MinimalangLexer;
import com.writeoncereadmany.minimalang.generated.MinimalangParser;
import org.antlr.v4.runtime.CommonTokenStream;

import static org.antlr.v4.runtime.CharStreams.fromString;

/**
 * Created by tomj on 21/07/2017.
 */
public class Compiler {

    public Program compile(String program) {
        MinimalangLexer lexer = new MinimalangLexer(fromString(program));
        lexer.addErrorListener(new FailFastErrorListener(LexException::new));

        MinimalangParser parser = new MinimalangParser(new CommonTokenStream(lexer));
        parser.addErrorListener(new FailFastErrorListener(ParseException::new));

        return ProgramBuilder.build(parser.program());
    }

}
