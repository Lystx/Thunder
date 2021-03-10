
package io.vera.server.logger;

import javax.annotation.concurrent.Immutable;


@Immutable
class NoDebugLogger extends PipelinedLogger {

    public NoDebugLogger(PipelinedLogger next) {
        super(next);
    }

    @Override
    public LogMessageImpl handle(LogMessageImpl msg) {
        return msg;
    }

    @Override
    public void debug(LogMessageImpl msg) {
        // No-op
    }
}

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

    @Override
    public LogMessageImpl handle(LogMessageImpl msg) {
        return msg;
    }
}