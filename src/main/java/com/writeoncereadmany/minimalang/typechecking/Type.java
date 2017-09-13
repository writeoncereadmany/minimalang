package com.writeoncereadmany.minimalang.typechecking;

import java.util.Optional;

public interface Type {

    Optional<TypeError> assign(Type other, Types types);
}
