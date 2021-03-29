package io.vera.server.logger;

import javax.annotation.concurrent.Immutable;

@Immutable
public class DebugLogger extends PipelinedLogger {
  protected DebugLogger(PipelinedLogger next) {
    super(next);
  }
  
  public static PipelinedLogger verbose(PipelinedLogger next) {
    return new DebugLogger(next);
  }
  
  public static PipelinedLogger noop(PipelinedLogger next) {
    return new NoDebugLogger(next);
  }
  
  public LogMessageImpl handle(LogMessageImpl msg) {
    return msg;
  }
}
