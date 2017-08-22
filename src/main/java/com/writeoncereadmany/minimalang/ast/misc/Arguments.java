package com.writeoncereadmany.minimalang.ast.misc;

import com.writeoncereadmany.minimalang.ast.expressions.Expression;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Created by tomj on 27/07/2017.
 */
public class Arguments {

    private List<Expression> expressions;

    public Arguments(List<Expression> expressions) {
        this.expressions = expressions;
    }

    public <T> List<T> fold(Expression.Catamorphism<T> cata) {
        return expressions
                .stream()
                .map(e -> e.fold(cata))
                .collect(toList());
    }
}
