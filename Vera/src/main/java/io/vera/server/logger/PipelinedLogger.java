
package io.vera.server.logger;

import javax.annotation.concurrent.Immutable;
import java.io.OutputStream;


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

    public abstract LogMessageImpl handle(LogMessageImpl msg);

    public PipelinedLogger next() {
        return this.next;
    }

    public void log(LogMessageImpl msg) {
        if (msg == null) return;
        this.next.log(this.handle(msg));
    }

    public void success(LogMessageImpl msg) {
        if (msg == null) return;
        this.next.success(this.handle(msg));
    }

    public void warn(LogMessageImpl msg) {
        if (msg == null) return;
        this.next.warn(this.handle(msg));
    }

    public void error(LogMessageImpl msg) {
        if (msg == null) return;
        this.next.error(this.handle(msg));
    }

    public void debug(LogMessageImpl msg) {
        if (msg == null) return;
        this.next.debug(this.handle(msg));
    }

    public OutputStream out() {
        return this.next.out();
    }
}