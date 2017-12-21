package com.writeoncereadmany.minimalang.ast;

import co.unruly.control.pair.Pair;
import com.writeoncereadmany.minimalang.ast.CataFunctions.BiInterpreter;
import com.writeoncereadmany.minimalang.ast.CataFunctions.Interpreter;

import java.util.List;
import java.util.Map;

import static co.unruly.control.ApplicableWrapper.startWith;
import static com.writeoncereadmany.minimalang.util.MapUtils.mapValues;
import static java.util.stream.Collectors.toList;

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
            return cata.onFunctionTypeDefinition.apply(
                paramTypes
                    .stream()
                    .map(def -> def.fold(cata, context).left)
                    .collect(toList()),
                returnType.fold(cata, context).left,
                context);
        }
    }

    private static class InterfaceTypeDefinition extends TypeDefinition {
        private final Map<String, TypeDefinition> fieldTypes;

        private InterfaceTypeDefinition(Map<String, TypeDefinition> fieldTypes) {
            this.fieldTypes = fieldTypes;
        }

        @Override
        public <T, C> Pair<T, C> fold(Catamorphism<T, C> cata, C context) {
            Map<String, T> fields = startWith(fieldTypes).then(mapValues(def -> def.fold(cata, context).left));
            return cata.onInterfaceTypeDefinition.apply(fields, context);
        }
    }

    private static class NamedType extends TypeDefinition {
        private final String name;

        private NamedType(String name) {
            this.name = name;
        }

        @Override
        public <T, C> Pair<T, C> fold(Catamorphism<T, C> cata, C context) {
            return cata.onNamedTypeDefinition.apply(name, context);
        }
    }

    public static class Catamorphism<T, C> {
        public final BiInterpreter<List<T>, T, T, C> onFunctionTypeDefinition;
        public final Interpreter<Map<String, T>, T, C> onInterfaceTypeDefinition;
        public final Interpreter<String, T, C> onNamedTypeDefinition;


        public Catamorphism(
            BiInterpreter<List<T>, T, T, C> onFunctionTypeDefinition,
            Interpreter<Map<String, T>, T, C> onInterfaceTypeDefinition,
            Interpreter<String, T, C> onNamedTypeDefinition
        ) {
            this.onFunctionTypeDefinition = onFunctionTypeDefinition;
            this.onInterfaceTypeDefinition = onInterfaceTypeDefinition;
            this.onNamedTypeDefinition = onNamedTypeDefinition;
        }
    }
}
