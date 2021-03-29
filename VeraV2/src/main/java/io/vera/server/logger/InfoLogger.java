package io.vera.server.logger;

import io.vera.logger.Logger;
import java.io.OutputStream;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.TextStyle;
import java.time.temporal.ChronoField;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class InfoLogger extends LoggerHandlers implements Logger {
  private static final DateTimeFormatter TIME_FORMAT = (new DateTimeFormatterBuilder())
    .appendValue(ChronoField.HOUR_OF_DAY, 2)
    .appendLiteral(':')
    .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
    .appendLiteral(':')
    .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
    .toFormatter();
  
  private static final DateTimeFormatter DATE_FORMAT = (new DateTimeFormatterBuilder())
    .appendText(ChronoField.MONTH_OF_YEAR, TextStyle.SHORT)
    .appendLiteral(' ')
    .appendValue(ChronoField.DAY_OF_MONTH, 2)
    .appendLiteral(' ')
    .appendValue(ChronoField.YEAR, 4)
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
    return CACHE.computeIfAbsent(name, k -> new InfoLogger(next, name));
  }
  
  private LogMessageImpl handle(String level, String s) {
    ZonedDateTime time = ZonedDateTime.now();
    String[] components = { time.format(DATE_FORMAT), time.format(TIME_FORMAT), "[" + this.name + "/" + level + "]" };
    return handle(new LogMessageImpl(this, components, s, time));
  }
  
  public String getName() {
    return this.name;
  }
  
  public void log(String s) {
    this.next.log(handle("INFO", s));
  }
  
  public void success(String s) {
    this.next.success(handle("INFO", s));
  }
  
  public void warn(String s) {
    this.next.warn(handle("WARN", s));
  }
  
  public void error(String s) {
    this.next.error(handle("ERROR", s));
  }
  
  public void debug(String s) {
    this.next.debug(handle("DEBUG", s));
  }
  
  public OutputStream getOutputStream() {
    return this.next.out();
  }
}
