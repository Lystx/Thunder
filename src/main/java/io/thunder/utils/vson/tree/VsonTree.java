package io.thunder.utils.vson.tree;

import io.thunder.utils.vson.VsonValue;
import io.thunder.utils.vson.annotation.other.Vson;
import io.thunder.utils.vson.elements.object.VsonObject;

public interface VsonTree<T> {

    T from(VsonObject vsonObject, Class<T> tClass) throws Exception;

    VsonValue toVson() throws Exception;

    static <T> VsonTree<T> newTree(Class<T> tClass) {
        return newTree(null);
    }

    static <T> VsonTree<T> newTree(T object) {
        return Vson.get().createTree(object);
    }
}
