package com.writeoncereadmany.minimalang.typechecking;

import com.writeoncereadmany.minimalang.ast.TypeDefinition.Catamorphism;
import com.writeoncereadmany.minimalang.typechecking.types.ConcreteFunctionType;
import com.writeoncereadmany.minimalang.typechecking.types.NamedType;
import com.writeoncereadmany.minimalang.typechecking.types.ObjectType;

import static com.writeoncereadmany.minimalang.ast.CataFunctions.contextFree;

public interface TypeDefiner {

    static Catamorphism<Type, Types> typeDefiner() {
        return new Catamorphism<>(
            contextFree(ConcreteFunctionType::new),
            contextFree(ObjectType::new),
            contextFree(NamedType::new)
        );
    }
}
