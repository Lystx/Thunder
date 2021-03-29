package io.vera.server.logger;

import io.vera.logger.LogHandler;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class LoggerHandlers extends PipelinedLogger {
  private final Set<LogHandler> handlers = Collections.newSetFromMap(new ConcurrentHashMap<>());
  
  public LoggerHandlers(PipelinedLogger next) {
    super(next);
  }
  
  public LogMessageImpl handle(LogMessageImpl msg) {
    boolean doLog = true;
    for (LogHandler handler : this.handlers) {
      if (!handler.handle(msg))
        doLog = false; 
    } 
    return doLog ? msg : null;
  }
  
  public Set<LogHandler> handlers() {
    return this.handlers;
  }
}
