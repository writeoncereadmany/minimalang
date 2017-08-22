package com.writeoncereadmany.minimalang.ast.expressions;

import com.writeoncereadmany.minimalang.ast.misc.Arguments;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

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

    public static Expression sequence(List<Expression> expressions) {
        return new Sequence(expressions);
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
            return cata.onCall.apply(
                    function.fold(cata),
                    arguments.fold(cata)
            );
        }
    }

    private static class StringLiteral extends Expression {
        private final String text;

        public StringLiteral(String text) {
            this.text = text;
        }

        @Override
        public <T> T fold(Catamorphism<T> cata) {
            return cata.onStringLiteral.apply(text);
        }
    }

    private static class Variable extends Expression {
        private final String name;

        public Variable(String name) {
            this.name = name;
        }

        @Override
        public <T> T fold(Catamorphism<T> cata) {
            return cata.onVariable.apply(name);
        }
    }

    private static class Sequence extends Expression {
        private final List<Expression> expressions;

        public Sequence(List<Expression> expressions) {
            this.expressions = expressions;
        }

        @Override
        public <T> T fold(Catamorphism<T> cata) {
            return cata.onSequence.apply(expressions.stream().map(e -> e.fold(cata)).collect(toList()));
        }
    }

    /*************************************
     *
     * Catamorphism holder
     *
     *************************************/

    public static class Catamorphism<T> {
        public final BiFunction<T, List<T>, T> onCall;
        public final Function<String, T> onStringLiteral;
        public final Function<String, T> onVariable;
        public final Function<List<T>, T> onSequence;

        public Catamorphism(
            BiFunction<T, List<T>, T> onCall,
            Function<String, T> onStringLiteral,
            Function<String, T> onVariable,
            Function<List<T>, T> onSequence
        ) {
            this.onCall = onCall;
            this.onStringLiteral = onStringLiteral;
            this.onVariable = onVariable;
            this.onSequence = onSequence;
        }
    }
}
