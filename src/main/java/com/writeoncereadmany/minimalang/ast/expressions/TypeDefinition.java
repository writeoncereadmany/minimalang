package com.writeoncereadmany.minimalang.ast.expressions;

import co.unruly.control.pair.Pair;

import java.util.List;
import java.util.Map;

public abstract class TypeDefinition {

    private TypeDefinition() {}

    public static TypeDefinition functionTypeDefinition(List<TypeDefinition> paramTypes, TypeDefinition returnType) {
        return new FunctionTypeDefinition(paramTypes, returnType);
    }

    public static TypeDefinition interfaceTypeDefinition(Map<String, TypeDefinition> fieldTypes) {
        return new InterfaceTypeDefinition(fieldTypes);
    }

    public static TypeDefinition namedTypeDefinition(String name) {
        return new NamedType(name);
    }

    public abstract <T, C> Pair<T, C> fold(Catamorphism<T, C> cata, C context);

    private static class FunctionTypeDefinition extends TypeDefinition {
        private final List<TypeDefinition> paramTypes;
        private final TypeDefinition returnType;

        private FunctionTypeDefinition(List<TypeDefinition> paramTypes, TypeDefinition returnType) {
            this.paramTypes = paramTypes;
            this.returnType = returnType;
        }

        @Override
        public <T, C> Pair<T, C> fold(Catamorphism<T, C> cata, C context) {
            return null;
        }
    }

    private static class InterfaceTypeDefinition extends TypeDefinition {
        private final Map<String, TypeDefinition> fieldTypes;

        private InterfaceTypeDefinition(Map<String, TypeDefinition> fieldTypes) {
            this.fieldTypes = fieldTypes;
        }

        @Override
        public <T, C> Pair<T, C> fold(Catamorphism<T, C> cata, C context) {
            return null;
        }
    }

    private static class NamedType extends TypeDefinition {
        private final String name;

        private NamedType(String name) {
            this.name = name;
        }

        @Override
        public <T, C> Pair<T, C> fold(Catamorphism<T, C> cata, C context) {
            return null;
        }
    }

    public static class Catamorphism<T, C> {
        public final Expression.BiInterpreter<List<T>, T, T, C> onFunctionTypeDefinition;
        public final Expression.Interpreter<Map<String, T>, T, C> onInterfaceTypeDefinition;
        public final Expression.Interpreter<String, T, C> onNamedTypeDefinition;


        public Catamorphism(
            Expression.BiInterpreter<List<T>, T, T, C> onFunctionTypeDefinition,
            Expression.Interpreter<Map<String, T>, T, C> onInterfaceTypeDefinition,
            Expression.Interpreter<String, T, C> onNamedTypeDefinition
        ) {
            this.onFunctionTypeDefinition = onFunctionTypeDefinition;
            this.onInterfaceTypeDefinition = onInterfaceTypeDefinition;
            this.onNamedTypeDefinition = onNamedTypeDefinition;
        }
    }
}
