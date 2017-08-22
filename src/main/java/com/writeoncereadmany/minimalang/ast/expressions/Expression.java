package com.writeoncereadmany.minimalang.ast.expressions;

import co.unruly.control.PartialApplication.TriFunction;
import co.unruly.control.pair.Pair;
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

    public abstract <T, C> Pair<T, C> fold(Catamorphism<T, C> cata, C context);

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
        public <T, C> Pair<T, C> fold(Catamorphism<T, C> cata, C context) {
            return cata.onCall.apply(
                    function.fold(cata, context).left,
                    arguments.fold(cata, context).stream().map(Pair::left).collect(toList()),
                    context
            );
        }
    }

    private static class StringLiteral extends Expression {
        private final String text;

        public StringLiteral(String text) {
            this.text = text;
        }

        @Override
        public <T, C> Pair<T, C> fold(Catamorphism<T, C> cata, C context) {
            return cata.onStringLiteral.apply(text, context);
        }
    }

    private static class Variable extends Expression {
        private final String name;

        public Variable(String name) {
            this.name = name;
        }

        @Override
        public <T, C> Pair<T, C> fold(Catamorphism<T, C> cata, C context) {
            return cata.onVariable.apply(name, context);
        }
    }

    private static class Sequence extends Expression {
        private final List<Expression> expressions;

        public Sequence(List<Expression> expressions) {
            this.expressions = expressions;
        }


        @Override
        public <T, C> Pair<T, C> fold(Catamorphism<T, C> cata, C context) {
            return cata.onSequence.apply(expressions.stream().map(e -> e.fold(cata, context)).map(Pair::left).collect(toList()), context);
        }
    }

    /*************************************
     *
     * Catamorphism holder
     *
     *************************************/

    public static class Catamorphism<T, C> {
        public final TriFunction<T, List<T>, C, Pair<T, C>> onCall;
        public final BiFunction<String, C, Pair<T, C>> onStringLiteral;
        public final BiFunction<String, C, Pair<T, C>> onVariable;
        public final BiFunction<List<T>, C, Pair<T, C>> onSequence;

        public Catamorphism(
            TriFunction<T, List<T>, C, Pair<T, C>> onCall,
            BiFunction<String, C, Pair<T, C>> onStringLiteral,
            BiFunction<String, C, Pair<T, C>> onVariable,
            BiFunction<List<T>, C, Pair<T, C>> onSequence
        ) {
            this.onCall = onCall;
            this.onStringLiteral = onStringLiteral;
            this.onVariable = onVariable;
            this.onSequence = onSequence;
        }
    }
}
