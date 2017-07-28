package com.writeoncereadmany.minstrel.ast.expressions;

import com.writeoncereadmany.minstrel.ast.misc.Arguments;

/**
 * Created by tomj on 27/07/2017.
 */
public class Call implements Expression {
    private final Expression function;
    private final Arguments arguments;

    public Call(Expression function, Arguments arguments) {
        this.function = function;
        this.arguments = arguments;
    }
}
