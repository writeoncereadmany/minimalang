package com.writeoncereadmany.minimalang.typechecking;

import java.util.List;

public interface Type {

    List<TypeError> assign(Type other, Types types);
}
