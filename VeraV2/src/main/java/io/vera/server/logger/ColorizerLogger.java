package io.vera.server.logger;

import io.vera.ui.chat.ChatColor;
import javax.annotation.concurrent.Immutable;

@Immutable
public class ColorizerLogger extends PipelinedLogger {
  public static final String RESET = "\033[0m";
  
  public static final String BLACK = "\033[30m";
  
  public static final String RED = "\033[31m";
  
  public static final String GREEN = "\033[32m";
  
  public static final String YELLOW = "\033[33m";
  
  public static final String BLUE = "\033[34m";
  
  public static final String PURPLE = "\033[35m";
  
  public static final String CYAN = "\033[36m";
  
  public static final String WHITE = "\033[37m";
  
  public static final String BOLD = "\033[1m";
  
  public static final String ITALICS = "\033[3m";
  
  public static final String UNDERLINE = "\033[4m";
  
  public static final String STRIKETHROUGH = "\033[9m";
  
  public ColorizerLogger(PipelinedLogger next) {
    super(next);
  }
  
  public LogMessageImpl handle(LogMessageImpl msg) {
    char[] sq = msg.getMessage().toCharArray();
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < sq.length; i++) {
      char c = sq[i];
      if (c == ChatColor.getEscape()) {
        int codeIdx = ++i;
        if (codeIdx >= sq.length)
          break; 
        ChatColor decode = ChatColor.of(sq[codeIdx]);
        switch (decode) {
          case BLACK:
            builder.append("\033[30m");
            break;
          case DARK_BLUE:
            builder.append("\033[34m");
            break;
          case DARK_GREEN:
            builder.append("\033[32m");
            break;
          case DARK_AQUA:
            builder.append("\033[36m");
            break;
          case DARK_RED:
            builder.append("\033[31m");
            break;
          case DARK_PURPLE:
            builder.append("\033[35m");
            break;
          case GOLD:
            builder.append("\033[33m");
            break;
          case GRAY:
            builder.append("\033[37m");
            break;
          case DARK_GRAY:
            builder.append("\033[37m");
            break;
          case BLUE:
            builder.append("\033[34m");
            break;
          case GREEN:
            builder.append("\033[32m");
            break;
          case AQUA:
            builder.append("\033[36m");
            break;
          case RED:
            builder.append("\033[31m");
            break;
          case LIGHT_PURPLE:
            builder.append("\033[35m");
            break;
          case YELLOW:
            builder.append("\033[33m");
            break;
          case WHITE:
            builder.append("\033[37m");
            break;
          case OBFUSCATED:
            break;
          case BOLD:
            builder.append("\033[1m");
            break;
          case STRIKETHROUGH:
            builder.append("\033[9m");
            break;
          case UNDERLINE:
            builder.append("\033[4m");
            break;
          case ITALIC:
            builder.append("\033[3m");
            break;
          case RESET:
            builder.append("\033[0m");
            break;
          default:
            builder.append(ChatColor.getEscape()).append(sq[codeIdx]);
            break;
        } 
      } else {
        builder.append(c);
      } 
    } 
    msg.setMessage(builder + "\033[0m");
    return msg;
  }
  
  private LogMessageImpl handle(String color, LogMessageImpl msg) {
    msg.setMessage(color + msg.getMessage() + "\033[0m");
    return msg;
  }
  
  public void log(LogMessageImpl msg) {
    this.next.log(handle(msg));
  }
  
  public void success(LogMessageImpl msg) {
    this.next.success(handle("\033[32m", msg));
  }
  
  public void warn(LogMessageImpl msg) {
    this.next.warn(handle("\033[33m", msg));
  }
  
  public void error(LogMessageImpl msg) {
    this.next.error(handle("\033[31m", msg));
  }
  
  public void debug(LogMessageImpl msg) {
    this.next.debug(handle("\033[37m", msg));
  }
}
