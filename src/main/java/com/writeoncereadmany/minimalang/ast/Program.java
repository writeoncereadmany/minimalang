package com.writeoncereadmany.minimalang.ast;


import co.unruly.control.pair.Pair;

public class Program {

    private Expression expressions;

    public Program(Expression expressions) {
        this.expressions = expressions;
    }

    public <T, C> Pair<T, C> run(Expression.Catamorphism<T, C> cata, C initialContext) {
        return expressions.fold(cata, initialContext);
    }
}
