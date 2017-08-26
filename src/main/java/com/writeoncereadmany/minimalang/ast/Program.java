package com.writeoncereadmany.minimalang.ast;


import co.unruly.control.pair.Pair;
import com.writeoncereadmany.minimalang.ast.expressions.Expression;

import java.util.List;

public class Program {

    private List<Expression> expressions;

    public Program(List<Expression> expressions) {
        this.expressions = expressions;
    }

    public <T, C> Pair<T, C> run(Expression.Catamorphism<T, C> cata, C initialContext) {
        Expression firstExpression = expressions.get(0);
        Pair<T, C> current = firstExpression.fold(cata, initialContext);
        for(Expression next : expressions.subList(1, expressions.size())) {
            current = next.fold(cata, current.right);
        }
        return current;
    }
}
