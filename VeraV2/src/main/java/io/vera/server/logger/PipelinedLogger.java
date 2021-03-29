package io.vera.server.logger;

import java.io.OutputStream;
import javax.annotation.concurrent.Immutable;

@Immutable
public abstract class PipelinedLogger {
  protected final PipelinedLogger next;
  
  public PipelinedLogger(PipelinedLogger next) {
    this.next = next;
  }
  
  public static PipelinedLogger init(boolean verbose) throws Exception {
    PipelinedLogger underlying = new DefaultLogger();
    PipelinedLogger colorizer = new ColorizerLogger(underlying);
    PipelinedLogger debugger = verbose ? DebugLogger.verbose(colorizer) : DebugLogger.noop(colorizer);
    PipelinedLogger handler = new LoggerHandlers(debugger);
    return FileLogger.init(handler);
  }
  
  public abstract LogMessageImpl handle(LogMessageImpl paramLogMessageImpl);
  
  public PipelinedLogger next() {
    return this.next;
  }
  
  public void log(LogMessageImpl msg) {
    if (msg == null)
      return; 
    this.next.log(handle(msg));
  }
  
  public void success(LogMessageImpl msg) {
    if (msg == null)
      return; 
    this.next.success(handle(msg));
  }
  
  public void warn(LogMessageImpl msg) {
    if (msg == null)
      return; 
    this.next.warn(handle(msg));
  }
  
  public void error(LogMessageImpl msg) {
    if (msg == null)
      return; 
    this.next.error(handle(msg));
  }
  
  public void debug(LogMessageImpl msg) {
    if (msg == null)
      return; 
    this.next.debug(handle(msg));
  }
  
  public OutputStream out() {
    return this.next.out();
  }
}
