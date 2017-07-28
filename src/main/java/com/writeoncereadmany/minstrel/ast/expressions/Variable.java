package com.writeoncereadmany.minstrel.ast.expressions;

/**
 * Created by tomj on 27/07/2017.
 */
public class Variable implements Expression {
    private String name;

    public Variable(String name) {
        this.name = name;
    }
}
