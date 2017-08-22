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

    public static Expression sequence(Expression first, Expression second) {
        return new Sequence(first, second);
    }

    public static Expression declaration(String name, Expression expression) {
        return new Declaration(name, expression);
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
        private final Expression first;
        private final Expression second;

        public Sequence(Expression first, Expression second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public <T, C> Pair<T, C> fold(Catamorphism<T, C> cata, C context) {
            Pair<T, C> afterFirst = first.fold(cata, context);
            Pair<T, C> afterSecond = second.fold(cata, afterFirst.right);
            return cata.onSequence.apply(afterFirst.left, afterSecond.left, context);
        }
    }

    private static class Declaration extends Expression {
        private final String name;
        private final Expression expression;

        private Declaration(String name, Expression expression) {
            this.name = name;
            this.expression = expression;
        }

        @Override
        public <T, C> Pair<T, C> fold(Catamorphism<T, C> cata, C context) {
            final T value = expression.fold(cata, context).left;
            return cata.onDeclaration.apply(name, value, context);
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
        public final TriFunction<T, T, C, Pair<T, C>> onSequence;
        public final TriFunction<String, T, C, Pair<T, C>> onDeclaration;

        public Catamorphism(
            TriFunction<T, List<T>, C, Pair<T, C>> onCall,
            BiFunction<String, C, Pair<T, C>> onStringLiteral,
            BiFunction<String, C, Pair<T, C>> onVariable,
            TriFunction<T, T, C, Pair<T, C>> onSequence,
            TriFunction<String, T, C, Pair<T, C>> onDeclaration
        ) {
            this.onCall = onCall;
            this.onStringLiteral = onStringLiteral;
            this.onVariable = onVariable;
            this.onSequence = onSequence;
            this.onDeclaration = onDeclaration;
        }
    }
}
