package com.writeoncereadmany.minimalang.ast.misc;

import co.unruly.control.pair.Pair;
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

    public <T, C> List<Pair<T, C>> fold(Expression.Catamorphism<T, C> cata, C context) {
        return expressions
                .stream()
                .map(e -> e.fold(cata, context))
                .collect(toList());
    }
}
