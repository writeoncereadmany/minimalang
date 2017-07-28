package com.writeoncereadmany.minstrel.ast.expressions;

/**
 * Created by tomj on 27/07/2017.
 */
public class StringLiteral implements Expression {
    private String text;

    public StringLiteral(String text) {
        this.text = text;
    }
}
