package com.writeoncereadmany.minimalang.util;

import co.unruly.control.result.Result;

import java.util.List;
import java.util.function.Function;

public interface ListUtils {

    static <T> Function<List<T>, Result<T, List<T>>> extractSingleValue() {
        return items -> {
            if(items.size() == 1) {
                return Result.success(items.get(0));
            } else {
                return Result.failure(items);
            }
        };
    }
}
