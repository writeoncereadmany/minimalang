package com.writeoncereadmany.minimalang.ast.misc;

import com.writeoncereadmany.minimalang.ast.expressions.Expression;

import java.util.List;

/**
 * Created by tomj on 27/07/2017.
 */
public class Arguments {

    private List<Expression> expressions;

    public Arguments(List<Expression> expressions) {
        this.expressions = expressions;
    }
}
