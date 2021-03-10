
package io.vera.server.logger;

import io.vera.logger.Logger;
import io.vera.doc.Policy;

import javax.annotation.concurrent.ThreadSafe;
import java.io.OutputStream;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.TextStyle;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.time.temporal.ChronoField.*;

@ThreadSafe
public class InfoLogger extends LoggerHandlers implements Logger {

    private static final DateTimeFormatter TIME_FORMAT = new DateTimeFormatterBuilder()
            .appendValue(HOUR_OF_DAY, 2)
            .appendLiteral(':')
            .appendValue(MINUTE_OF_HOUR, 2)
            .appendLiteral(':')
            .appendValue(SECOND_OF_MINUTE, 2)
            .toFormatter();

    private static final DateTimeFormatter DATE_FORMAT = new DateTimeFormatterBuilder()
            .appendText(MONTH_OF_YEAR, TextStyle.SHORT)
            .appendLiteral(' ')
            .appendValue(DAY_OF_MONTH, 2)
            .appendLiteral(' ')
            .appendValue(YEAR, 4)
            .toFormatter();

    private static final Map<String, InfoLogger> CACHE = new ConcurrentHashMap<>();

    private static final String INFO = "INFO";
    private static final String WARN = "WARN";
    private static final String ERROR = "ERROR";
    private static final String DEBUG = "DEBUG";

    private final PipelinedLogger next;

    private final String name;

    public InfoLogger(PipelinedLogger next, String name) {
        super(null);
        this.next = next;
        this.name = name;
    }

    public static Logger get(PipelinedLogger next, String name) {
        return CACHE.computeIfAbsent(name, (k) -> new InfoLogger(next, name));
    }

    private LogMessageImpl handle(String level, String s) {
        ZonedDateTime time = ZonedDateTime.now();
        String[] components = { time.format(DATE_FORMAT),
                time.format(TIME_FORMAT),
                "[" + this.name + "/" + level + "]" };
        return super.handle(new LogMessageImpl(this, components, s, time));
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void log(String s) {
        this.next.log(this.handle(INFO, s));
    }

    @Override
    public void success(String s) {
        this.next.success(this.handle(INFO, s));
    }

    @Override
    public void warn(String s) {
        this.next.warn(this.handle(WARN, s));
    }

    @Override
    public void error(String s) {
        this.next.error(this.handle(ERROR, s));
    }

    @Override
    public void debug(String s) {
        this.next.debug(this.handle(DEBUG, s));
    }

    @Override
    public OutputStream getOutputStream() {
        return this.next.out();
    }
}
