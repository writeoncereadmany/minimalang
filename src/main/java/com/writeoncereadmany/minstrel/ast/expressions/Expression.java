package com.writeoncereadmany.minstrel.ast.expressions;

import com.writeoncereadmany.minstrel.ast.misc.Arguments;

import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class Expression {

    private Expression() {}

    /**************************************
     *
     * Visible constructors and interface
     *
     *************************************/


    public static Expression call(Expression function, Arguments arguments) {
        return new Call(function, arguments);
    }

    public static Expression stringLiteral(String text) {
        return new StringLiteral(text);
    }

    public static Expression variable(String name) {
        return new Variable(name);
    }

    public abstract <T> T fold(Catamorphism<T> cata);

    public <T> T then(Function<Expression, T> function) {
        return function.apply(this);
    }

    /*************************************
     *
     * Sum type implementations
     *
     ************************************/

    private static class Call extends Expression {
        private final Expression function;
        private final Arguments arguments;

        private Call(Expression function, Arguments arguments) {
            this.function = function;
            this.arguments = arguments;
        }

        @Override
        public <T> T fold(Catamorphism<T> cata) {
            return cata.onCall.apply(function.fold(cata), arguments);
        }
    }

    private static class StringLiteral extends Expression {
        private String text;

        public StringLiteral(String text) {
            this.text = text;
        }

        @Override
        public <T> T fold(Catamorphism<T> cata) {
            return cata.onStringLiteral.apply(text);
        }
    }

    private static class Variable extends Expression {
        private String name;

        public Variable(String name) {
            this.name = name;
        }

        @Override
        public <T> T fold(Catamorphism<T> cata) {
            return cata.onVariable.apply(name);
        }
    }

    /*************************************
     *
     * Catamorphism holder
     *
     *************************************/

    public static class Catamorphism<T> {
        public final BiFunction<T, Arguments, T> onCall;
        public final Function<String, T> onStringLiteral;
        public final Function<String, T> onVariable;

        public Catamorphism(BiFunction<T, Arguments, T> onCall, Function<String, T> onStringLiteral, Function<String, T> onVariable) {
            this.onCall = onCall;
            this.onStringLiteral = onStringLiteral;
            this.onVariable = onVariable;
        }
    }
}
