package com.writeoncereadmany.minimalang.util;

import co.unruly.control.pair.Maps;
import co.unruly.control.pair.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static co.unruly.control.ApplicableWrapper.startWith;

public interface MapUtils {

    static <K, V> Map<K, V> immutablePut(Map<K, V> original, K key, V value) {
        HashMap<K, V> newMap = new HashMap<>(original);
        newMap.put(key, value);
        return newMap;
    }

    static <OK, NK, V> Function<Map<OK, V>, Map<NK, V>> mapKeys(Function<OK, NK> f) {
        return map -> map
            .entrySet()
            .stream()
            .map(entry -> Pair.of(f.apply(entry.getKey()), entry.getValue()))
            .collect(Maps.toMap());
    }

    static <OK, NK, V> Map<NK, V> mapKeys(Function<OK, NK> f, Map<OK, V> m) {
        return startWith(m).then(mapKeys(f));
    }
}
