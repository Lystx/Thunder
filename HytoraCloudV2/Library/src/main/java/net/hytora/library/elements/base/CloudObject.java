package net.hytora.library.elements.base;

import java.util.UUID;

public interface CloudObject<T> {

    String getName();

    UUID getUniqueId();

    T get();
}
