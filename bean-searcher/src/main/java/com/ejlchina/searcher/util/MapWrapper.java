package com.ejlchina.searcher.util;

import java.util.*;
import java.util.stream.Collectors;

public class MapWrapper {

    private final Map<String, Object> map;
    private final String prefix;

    public MapWrapper(Map<String, Object> map, String prefix) {
        this.map = Objects.requireNonNull(map);
        this.prefix = prefix;
    }

    transient Set<String> keySet;

    public Set<String> keySet() {
        Set<String> ks = keySet;
        if (ks == null) {
            if (prefix != null) {
                ks = map.keySet().stream().map(k -> prefix + k).collect(Collectors.toSet());
            } else {
                ks = map.keySet();
            }
            keySet = ks;
        }
        return ks;
    }

    public Object get(String key) {
        if (prefix != null) {
            return map.get(prefix + key);
        }
        return map.get(key);
    }

}