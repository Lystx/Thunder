package io.thunder.utils.vson.annotation.other;

import io.thunder.utils.vson.VsonValue;
import io.thunder.utils.vson.manage.vson.VsonWriter;

public interface VsonAdapter<T> {

    VsonValue write(T t, VsonWriter vsonWriter);

    T read(VsonValue vsonValue);

    Class<T> getTypeClass();
}
