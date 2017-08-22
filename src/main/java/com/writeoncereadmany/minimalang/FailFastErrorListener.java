package com.writeoncereadmany.minimalang;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;

import java.util.BitSet;
import java.util.function.Function;

public class FailFastErrorListener implements ANTLRErrorListener {

    private final Function<String, ? extends RuntimeException> exceptionFactory;

    public FailFastErrorListener(Function<String, ? extends RuntimeException> exceptionFactory) {
        this.exceptionFactory = exceptionFactory;
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        throw exceptionFactory.apply("Syntax error");
    }

    @Override
    public void reportAmbiguity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, boolean exact, BitSet ambigAlts, ATNConfigSet configs) {
        throw exceptionFactory.apply("Ambiguity");
    }

    @Override
    public void reportAttemptingFullContext(Parser recognizer, DFA dfa, int startIndex, int stopIndex, BitSet conflictingAlts, ATNConfigSet configs) {
        throw exceptionFactory.apply("Attempting full context");
    }

    @Override
    public void reportContextSensitivity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, int prediction, ATNConfigSet configs) {
        throw exceptionFactory.apply("Context sensitivity");
    }
}
