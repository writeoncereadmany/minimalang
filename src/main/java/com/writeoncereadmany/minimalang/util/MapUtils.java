package com.writeoncereadmany.minimalang.util;

import java.util.HashMap;
import java.util.Map;

public interface MapUtils {

    static <K, V> Map<K, V> immutablePut(Map<K, V> original, K key, V value) {
        HashMap<K, V> newMap = new HashMap<>(original);
        newMap.put(key, value);
        return newMap;
    }
}
