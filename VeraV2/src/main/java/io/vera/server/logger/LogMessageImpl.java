package io.vera.server.logger;

import io.vera.logger.LogMessage;
import io.vera.logger.Logger;
import java.time.ZonedDateTime;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class LogMessageImpl implements LogMessage {
  final InfoLogger source;
  
  private final String[] components;
  
  private volatile String message;
  
  private final ZonedDateTime time;
  
  public LogMessageImpl(InfoLogger source, String[] components, String message, ZonedDateTime time) {
    this.source = source;
    this.components = components;
    this.message = message;
    this.time = time;
  }
  
  public InfoLogger getLogger() {
    return this.source;
  }
  
  public String[] getComponents() {
    return this.components;
  }
  
  public String getMessage() {
    return this.message;
  }
  
  public void setMessage(String message) {
    this.message = message;
  }
  
  public ZonedDateTime getTime() {
    return this.time;
  }
  
  public String format(int start) {
    StringBuilder builder = new StringBuilder();
    for (int i = start; i < this.components.length; i++)
      builder.append(this.components[i]).append(' '); 
    builder.append(this.message);
    return builder.toString();
  }
}
