package com.writeoncereadmany.minimalang.ast;


import com.writeoncereadmany.minimalang.ast.expressions.Expression;

public class Program {

    private Expression expression;

    public Program(Expression expression) {
        this.expression = expression;
    }

    public <T, C> T run(Expression.Catamorphism<T, C> cata, C initialContext) {
        return expression.fold(cata, initialContext).left;
    }
}
