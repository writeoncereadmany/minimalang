package com.writeoncereadmany.minstrel;

import com.writeoncereadmany.minstrel.ast.Program;
import com.writeoncereadmany.minstrel.ast.ProgramBuilder;
import com.writeoncereadmany.minstrel.generated.MinstrelLexer;
import com.writeoncereadmany.minstrel.generated.MinstrelParser;
import org.antlr.v4.runtime.CommonTokenStream;

import static org.antlr.v4.runtime.CharStreams.fromString;

/**
 * Created by tomj on 21/07/2017.
 */
public class Compiler {

    public Program compile(String program) {
        MinstrelLexer lexer = new MinstrelLexer(fromString(program));
        lexer.addErrorListener(new FailFastErrorListener(LexException::new));

        MinstrelParser parser = new MinstrelParser(new CommonTokenStream(lexer));
        parser.addErrorListener(new FailFastErrorListener(ParseException::new));

        return ProgramBuilder.build(parser.program());
    }

}
