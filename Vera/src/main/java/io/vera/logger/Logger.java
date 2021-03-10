
package io.vera.logger;

import io.vera.Impl;

import javax.annotation.concurrent.ThreadSafe;
import java.io.OutputStream;

@ThreadSafe
public interface Logger {

    static Logger get(String name) {
        return Impl.get().newLogger(name);
    }


    static Logger get(Class<?> cls) {
        return Impl.get().newLogger(cls.getSimpleName());
    }

    String getName();

    void log(String s);

    void success(String s);

    void warn(String s);

    void error(String s);

    void debug(String s);

    OutputStream getOutputStream();
}
