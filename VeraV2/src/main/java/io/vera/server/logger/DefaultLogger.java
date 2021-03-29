package io.vera.server.logger;

import java.io.OutputStream;
import java.io.PrintStream;
import javax.annotation.concurrent.Immutable;
import org.fusesource.jansi.AnsiConsole;

@Immutable
public class DefaultLogger extends PipelinedLogger {
  private final PrintStream stream;
  
  public DefaultLogger() {
    super(null);
    this.stream = AnsiConsole.out;
    System.setOut(this.stream);
    System.setErr(this.stream);
  }
  
  public LogMessageImpl handle(LogMessageImpl msg) {
    return null;
  }
  
  public void log(LogMessageImpl msg) {
    this.stream.println(msg.format(1));
  }
  
  public void success(LogMessageImpl msg) {
    this.stream.println(msg.format(1));
  }
  
  public void warn(LogMessageImpl msg) {
    this.stream.println(msg.format(1));
  }
  
  public void error(LogMessageImpl msg) {
    this.stream.println(msg.format(1));
  }
  
  public void debug(LogMessageImpl msg) {
    this.stream.println(msg.format(1));
  }
  
  public OutputStream out() {
    return this.stream;
  }
}
