
package io.vera.server.logger;

import io.vera.logger.LogMessage;

import javax.annotation.concurrent.ThreadSafe;
import java.time.ZonedDateTime;


@ThreadSafe
public class LogMessageImpl implements LogMessage {

     final InfoLogger source;
    private final String[] components;
    private volatile String message;
    private final ZonedDateTime time;

    public LogMessageImpl(InfoLogger source, String[] components,
                          String message, ZonedDateTime time) {
        this.source = source;
        this.components = components;
        this.message = message;
        this.time = time;
    }

    @Override
    public InfoLogger getLogger() {
        return this.source;
    }

    @Override
    public String[] getComponents() {
        return this.components;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public ZonedDateTime getTime() {
        return this.time;
    }

    public String format(int start) {
        StringBuilder builder = new StringBuilder();
        for (int i = start; i < this.components.length; i++) {
            builder.append(this.components[i]).append(' ');
        }

        builder.append(this.message);
        return builder.toString();
    }
}
