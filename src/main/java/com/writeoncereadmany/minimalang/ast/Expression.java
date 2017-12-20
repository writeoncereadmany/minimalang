package com.writeoncereadmany.minimalang.ast;

import co.unruly.control.pair.Maps;
import co.unruly.control.pair.Pair;
import com.writeoncereadmany.minimalang.ast.CataFunctions.BiInterpreter;
import com.writeoncereadmany.minimalang.ast.CataFunctions.Interpreter;
import com.writeoncereadmany.minimalang.ast.CataFunctions.TriInterpreter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    public static Expression declaration(Introduction variable, Expression expression) {
        return new Declaration(variable, expression);
    }

    public static Expression variable(String name) {
        return new Variable(name);
    }

    public static Expression objectLiteral(List<Pair<Introduction, Expression>> fields) {
        return new ObjectLiteral(fields);
    }

    public static Expression access(Expression object, String field) {
        return new Access(object, field);
    }

    public static Expression function(List<Introduction> arguments, Expression body) {
        return new FunctionLiteral(arguments, body);
    }

    public static Expression call(Expression function, List<Expression> arguments) {
        return new Call(function, arguments);
    }

    public static Expression sequence(List<Expression> expressions) {
        return new Sequence(expressions);
    }

    public static Expression typeDeclaration(String name, TypeDefinition type) {
        return new TypeDeclaration(name, type);
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
        private final Introduction name;
        private final Expression expression;

        private Declaration(Introduction name, Expression expression) {
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
        private final List<Pair<Introduction, Expression>> values;

        private ObjectLiteral(List<Pair<Introduction, Expression>> values) {
            this.values = values;
        }

        @Override
        public <T, C> Pair<T, C> fold(Catamorphism<T, C> cata, C context) {
            Map<Introduction, T> collect = values
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

        private final List<Introduction> parameters;
        private final Expression body;

        private FunctionLiteral(List<Introduction> parameters, Expression body) {
            this.parameters = parameters;
            this.body = body;
        }

        @Override
        public <T, C> Pair<T, C> fold(Catamorphism<T, C> cata, C context) {
            return cata.onFunction.apply(parameters, body, cata, context);
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
            List<T> results = new ArrayList<>();
            Pair<T, C> current = firstExpression.fold(cata, context);
            results.add(current.left);
            for(Expression next : expressions.subList(1, expressions.size())) {
                current = next.fold(cata, current.right);
                results.add(current.left);
            }
            return cata.onSequence.apply(results, current.right);
        }
    }

    private static class TypeDeclaration extends Expression {
        private final String name;
        private final TypeDefinition type;

        private TypeDeclaration(String name, TypeDefinition type) {
            this.name = name;
            this.type = type;
        }

        @Override
        public <T, C> Pair<T, C> fold(Catamorphism<T, C> cata, C context) {
            return cata.onTypeDeclaration.apply(name, type, context);
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
        public final Interpreter<List<T>, T, C> onSequence;
        public final BiInterpreter<Introduction, T, T, C> onDeclaration;
        public final Interpreter<Map<Introduction, T>, T, C> onObjectLiteral;
        public final BiInterpreter<T, String, T, C> onAccess;
        public final TriInterpreter<List<Introduction>, Expression, Catamorphism<T, C>, T, C> onFunction;
        public final Interpreter<String, T, C> onNumberLiteral;
        public final BiInterpreter<String, TypeDefinition, T, C> onTypeDeclaration;

        public Catamorphism(
            Interpreter<String, T, C> onStringLiteral,
            Interpreter<String, T, C> onNumberLiteral,
            BiInterpreter<Introduction, T, T, C> onDeclaration,
            Interpreter<String, T, C> onVariable,
            Interpreter<Map<Introduction, T>, T, C> onObjectLiteral,
            BiInterpreter<T, String, T, C> onAccess,
            TriInterpreter<List<Introduction>, Expression, Catamorphism<T, C>, T, C> onFunction,
            TriInterpreter<T, List<T>, Catamorphism<T, C>, T, C> onCall,
            Interpreter<List<T>, T, C> onSequence,
            BiInterpreter<String, TypeDefinition, T, C> onTypeDeclaration
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
            this.onTypeDeclaration = onTypeDeclaration;
        }
    }

}
