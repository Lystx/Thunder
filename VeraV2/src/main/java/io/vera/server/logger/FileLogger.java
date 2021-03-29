package io.vera.server.logger;

import io.vera.util.Misc;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class FileLogger extends PipelinedLogger {
  private static final Path DIR = Misc.HOME_PATH.resolve("logs");
  
  private static final String LINE_SEP = System.getProperty("line.separator");
  
  private static final int MAX_LEN = 83886080;
  
  private static final String IDX_SEPARATOR = Pattern.quote(".");
  
  @GuardedBy("lock")
  private Writer out;
  
  @GuardedBy("lock")
  private Path current;
  
  private final Object lock = new Object();
  
  private FileLogger(PipelinedLogger next) {
    super(next);
  }
  
  public static FileLogger init(PipelinedLogger next) throws Exception {
    FileLogger logger = new FileLogger(next);
    if (!Files.exists(DIR, new java.nio.file.LinkOption[0]))
      Files.createDirectory(DIR, (FileAttribute<?>[])new FileAttribute[0]); 
    File[] files = DIR.toFile().listFiles();
    if (files != null && files.length > 0) {
      int idx = -1;
      File f = null;
      for (File file : files) {
        String[] split = file.getName().split(IDX_SEPARATOR);
        int i = Integer.parseInt(split[1]);
        if (i > idx) {
          idx = i;
          f = file;
        } 
      } 
      if (f == null)
        throw new RuntimeException(); 
      synchronized (logger.lock) {
        logger.makeNewLog(f.toPath());
      } 
    } else {
      synchronized (logger.lock) {
        logger.makeNewLog(0);
      } 
    } 
    return logger;
  }
  
  public LogMessageImpl handle(LogMessageImpl msg) {
    Writer out = check();
    try {
      out.write(msg.format(0));
      out.write(LINE_SEP);
      out.flush();
    } catch (IOException e) {
      throw new RuntimeException(e);
    } 
    return msg;
  }
  
  private Writer check() {
    if (ThreadLocalRandom.current().nextInt(50) == 1)
      synchronized (this.lock) {
        if (Files.size(this.current) > 83886080L)
          makeNewLog(this.current); 
        return this.out;
      }  
    synchronized (this.lock) {
      return this.out;
    } 
  }
  
  @GuardedBy("lock")
  private void makeNewLog(Path last) throws IOException {
    String[] split = last.toFile().getName().split(IDX_SEPARATOR);
    int curIdx = Integer.parseInt(split[1]) + 1;
    makeNewLog(curIdx);
  }
  
  @GuardedBy("lock")
  private void makeNewLog(int idx) throws IOException {
    Path path = DIR.resolve("log." + idx + ".log");
    Files.createFile(path, (FileAttribute<?>[])new FileAttribute[0]);
    this.current = path;
    this.out = new FileWriter(path.toFile());
  }
}