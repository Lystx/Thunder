package io.vera.server.logger;

import javax.annotation.concurrent.Immutable;

@Immutable
class NoDebugLogger extends PipelinedLogger {
  public NoDebugLogger(PipelinedLogger next) {
    super(next);
  }
  
  public LogMessageImpl handle(LogMessageImpl msg) {
    return msg;
  }
  
  public void debug(LogMessageImpl msg) {}
}
