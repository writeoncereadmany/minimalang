package com.writeoncereadmany.minimalang.ast.expressions;

import co.unruly.control.PartialApplication;
import co.unruly.control.pair.Maps;
import co.unruly.control.pair.Pair;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import static co.unruly.control.pair.Pairs.onRight;
import static java.util.stream.Collectors.toList;

public abstract class Expression {

    private Expression() {}

    /**************************************
     *
     * Visible constructors and interface
     *
     *************************************/


    public static Expression stringLiteral(String text) {
        return new StringLiteral(text);
    }

    public static Expression numberLiteral(String number) {
        return new NumberLiteral(number);
    }

    public static Expression declaration(String name, Expression expression) {
        return new Declaration(name, expression);
    }

    public static Expression variable(String name) {
        return new Variable(name);
    }

    public static Expression objectLiteral(List<Pair<String, Expression>> fields) {
        return new ObjectLiteral(fields);
    }

    public static Expression access(Expression object, String field) {
        return new Access(object, field);
    }

    public static Expression function(List<String> arguments, Expression body) {
        return new FunctionLiteral(arguments, body);
    }

    public static Expression call(Expression function, List<Expression> arguments) {
        return new Call(function, arguments);
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

    private static class NumberLiteral extends Expression {
        private final String text;

        public NumberLiteral(String text) {
            this.text = text;
        }

        @Override
        public <T, C> Pair<T, C> fold(Catamorphism<T, C> cata, C context) {
            return cata.onNumberLiteral.apply(text, context);
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

    private static class ObjectLiteral extends Expression {
        private final List<Pair<String, Expression>> values;

        private ObjectLiteral(List<Pair<String, Expression>> values) {
            this.values = values;
        }

        @Override
        public <T, C> Pair<T, C> fold(Catamorphism<T, C> cata, C context) {
            Map<String, T> collect = values
                .stream()
                .map(field -> field.then(onRight(exp -> exp.fold(cata, context))))
                .map(field -> field.then(onRight(Pair::left)))
                .collect(Maps.toMap());
            return cata.onObjectLiteral.apply(collect, context);
        }
    }

    private static class Access extends Expression {

        private final Expression object;
        private final String field;

        public Access(Expression object, String field) {
            this.object = object;
            this.field = field;
        }

        @Override
        public <T, C> Pair<T, C> fold(Catamorphism<T, C> cata, C context) {
            return cata.onAccess.apply(object.fold(cata, context).left, field, context);
        }
    }

    private static class FunctionLiteral extends Expression {

        private final List<String> parameters;
        private final Expression body;

        private FunctionLiteral(List<String> parameters, Expression body) {
            this.parameters = parameters;
            this.body = body;
        }

        @Override
        public <T, C> Pair<T, C> fold(Catamorphism<T, C> cata, C context) {
            return cata.onFunction.apply(parameters, body, context);
        }
    }

    private static class Call extends Expression {
        private final Expression function;
        private final List<Expression> arguments;

        private Call(Expression function, List<Expression> arguments) {
            this.function = function;
            this.arguments = arguments;
        }

        @Override
        public <T, C> Pair<T, C> fold(Catamorphism<T, C> cata, C context) {
            return cata.onCall.apply(
                function.fold(cata, context).left,
                arguments.stream().map(arg -> arg.fold(cata, context)).map(Pair::left).collect(toList()),
                cata,
                context
            );
        }
    }

    private static class Sequence extends Expression {
        private final List<Expression> expressions;

        public Sequence(List<Expression> expressions) {
            this.expressions = expressions;
        }

        @Override
        public <T, C> Pair<T, C> fold(Catamorphism<T, C> cata, C context) {
            Expression firstExpression = expressions.get(0);
            Pair<T, C> current = firstExpression.fold(cata, context);
            for(Expression next : expressions.subList(1, expressions.size())) {
                current = next.fold(cata, current.right);
            }
            return cata.onSequence.apply(current.left, current.right);
        }
    }

    /*************************************
     *
     * Catamorphism holder
     *
     *************************************/

    public static class Catamorphism<T, C> {
        public final TriInterpreter<T, List<T>, Catamorphism<T, C>, T, C> onCall;
        public final Interpreter<String, T, C> onStringLiteral;
        public final Interpreter<String, T, C> onVariable;
        public final Interpreter<T, T, C> onSequence;
        public final BiInterpreter<String, T, T, C> onDeclaration;
        public final Interpreter<Map<String, T>, T, C> onObjectLiteral;
        public final BiInterpreter<T, String, T, C> onAccess;
        public final BiInterpreter<List<String>, Expression, T, C> onFunction;
        public final Interpreter<String, T, C> onNumberLiteral;

        public Catamorphism(
            Interpreter<String, T, C> onStringLiteral,
            Interpreter<String, T, C> onNumberLiteral,
            BiInterpreter<String, T, T, C> onDeclaration,
            Interpreter<String, T, C> onVariable,
            Interpreter<Map<String, T>, T, C> onObjectLiteral,
            BiInterpreter<T, String, T, C> onAccess,
            BiInterpreter<List<String>, Expression, T, C> onFunction,
            TriInterpreter<T, List<T>, Catamorphism<T, C>, T, C> onCall,
            Interpreter<T, T, C> onSequence
        ) {
            this.onCall = onCall;
            this.onStringLiteral = onStringLiteral;
            this.onVariable = onVariable;
            this.onSequence = onSequence;
            this.onDeclaration = onDeclaration;
            this.onObjectLiteral = onObjectLiteral;
            this.onAccess = onAccess;
            this.onFunction = onFunction;
            this.onNumberLiteral = onNumberLiteral;
        }
    }

    /*************************************
     *
     * Catamorphism functional interfaces
     *
     *************************************/

    @FunctionalInterface
    public interface Interpreter<X, T, C> {
        Pair<T, C> apply(X x, C context);
    }

    @FunctionalInterface
    public interface BiInterpreter<X, Y, T, C>  {
        Pair<T, C> apply(X x, Y y, C context);
    }

    @FunctionalInterface
    public interface TriInterpreter<X, Y, Z, T, C>  {
        Pair<T, C> apply(X x, Y y, Z z, C context);
    }

    @FunctionalInterface
    public interface QuadFunction<A, B, C, D, R> {
        R apply(A a, B b, C c, D d);
    }

    /*************************************
     *
     * Catamorphism context-free helpers
     *
     *************************************/

    public static <E, T, C> Expression.Interpreter<E, T, C> contextFree(Function<E, T> contextFreeFunction) {
        return (e, c) -> Pair.of(contextFreeFunction.apply(e), c);
    }

    public static <A, B, T, C> Expression.BiInterpreter<A, B, T, C> contextFree(BiFunction<A, B, T> contextFreeFunction) {
        return (a, b, c) -> Pair.of(contextFreeFunction.apply(a, b), c);
    }

    public static <X, Y, Z, T, C> Expression.TriInterpreter<X, Y, Z, T, C> contextFree(PartialApplication.TriFunction<X, Y, Z, T> contextFreeFunction) {
        return (x, y, z, c) -> Pair.of(contextFreeFunction.apply(x,y,z), c);
    }

    public static <E, T, C> Expression.Interpreter<E, T, C> usingContext(BiFunction<E, C, T> contextUsingFunction) {
        return (e, c) -> Pair.of(contextUsingFunction.apply(e, c), c);
    }

    public static <A, B, T, C> Expression.BiInterpreter<A, B, T, C> usingContext(PartialApplication.TriFunction<A, B, C, T> contextUsingFunction) {
        return (a, b, c) -> Pair.of(contextUsingFunction.apply(a, b, c), c);
    }

    public static <X, Y, Z, C, T> Expression.TriInterpreter<X, Y, Z, T, C> usingContext(QuadFunction<X, Y, Z, C, T> contextUsingFunction) {
        return (x, y, z, c) -> Pair.of(contextUsingFunction.apply(x, y, z, c), c);
    }
}
