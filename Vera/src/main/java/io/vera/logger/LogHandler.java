
package io.vera.logger;

import io.vera.Impl;

public interface LogHandler {

    static void intercept(Logger logger, LogHandler handler) {
        Impl.get().attachHandler(logger, handler);
    }

    static boolean removeHandler(Logger logger, LogHandler handler) {
        return Impl.get().removeHandler(logger, handler);
    }

    boolean handle(LogMessage message);
}