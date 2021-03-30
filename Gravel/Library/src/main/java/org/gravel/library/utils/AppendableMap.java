package org.gravel.library.utils;

import java.util.HashMap;

public class AppendableMap<K, V> extends HashMap<K, V> {

    public AppendableMap<K, V> append(K k, V v) {
        this.put(k, v);
        return this;
    }
}
