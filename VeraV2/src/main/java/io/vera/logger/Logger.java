package io.vera.logger;

import io.vera.Impl;
import java.io.OutputStream;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public interface Logger {
    static Logger get(String name) {
        return Impl.get().newLogger(name);
    }

    static Logger get(Class<?> cls) {
        return Impl.get().newLogger(cls.getSimpleName());
    }

    String getName();

    void log(String paramString);

    void success(String paramString);

    void warn(String paramString);

    void error(String paramString);

    void debug(String paramString);

    OutputStream getOutputStream();
}
