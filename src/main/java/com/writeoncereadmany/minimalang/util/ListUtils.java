package com.writeoncereadmany.minimalang.util;

import co.unruly.control.pair.Pair;

import java.util.List;
import java.util.Optional;

public interface ListUtils {

    static <T> Optional<Pair<T, List<T>>> headAndTail(List<T> items) {
        if(items.isEmpty()) {
            return Optional.empty();
        }
        else {
            return Optional.of(Pair.of(items.get(0), items.subList(1, items.size() - 1)));
        }
    }
}
