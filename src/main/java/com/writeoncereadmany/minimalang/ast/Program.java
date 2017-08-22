package com.writeoncereadmany.minimalang.ast;


import com.writeoncereadmany.minimalang.ast.expressions.Expression;

public class Program {

    private Expression expression;

    public Program(Expression expression) {
        this.expression = expression;
    }

    public <T> T run(Expression.Catamorphism<T> cata) {
        return expression.fold(cata);
    }
}
