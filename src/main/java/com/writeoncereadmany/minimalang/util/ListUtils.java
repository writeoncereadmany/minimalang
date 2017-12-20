package com.writeoncereadmany.minimalang.util;

import co.unruly.control.pair.Pair;
import co.unruly.control.result.Result;

import java.util.List;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

public interface ListUtils {

    static <T> List<T> flatten(List<List<T>> listOfLists) {
        return listOfLists.stream().flatMap(List::stream).collect(toList());
    }

    static <T> Function<List<T>, Result<T, List<T>>> extractSingleValue() {
        return items -> {
            if(items.size() == 1) {
                return Result.success(items.get(0));
            } else {
                return Result.failure(items);
            }
        };
    }

    static <T> Function<List<T>, Result<Pair<T, T>, List<T>>> extractTwoValues() {
        return items -> {
            if(items.size() == 2) {
                return Result.success(Pair.of(items.get(0), items.get(1)));
            } else {
                return Result.failure(items);
            }
        };
    }
}
