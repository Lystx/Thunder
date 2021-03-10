
package io.vera.server.logger;

import org.fusesource.jansi.AnsiConsole;

import javax.annotation.concurrent.Immutable;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * This logger is normally regarded as the underlying
 * logger as it is designed to be the final logger in the
 * pipeline; it does not pass the logger messages any
 * further and logs its output directly to the output.
 *
 * <p>Thus such, this class contains the init code required
 * to setup the loggers.</p>
 */
@Immutable
public class DefaultLogger extends PipelinedLogger {
    /**
     * The underlying stream to which output is passed
     */
    private final PrintStream stream;

    /**
     * Creates a new logger that prints messages to the out
     * stream at the bottom of the pipeline.
     */
    public DefaultLogger() {
        super(null);
        this.stream = AnsiConsole.out;

        System.setOut(this.stream);
        System.setErr(this.stream);
        // force error stream to
        // lock on System.out
    }

    @Override
    public LogMessageImpl handle(LogMessageImpl msg) {
        return null;
    }

    @Override
    public void log(LogMessageImpl msg) {
        this.stream.println(msg.format(1));
    }

    @Override
    public void success(LogMessageImpl msg) {
        this.stream.println(msg.format(1));
    }

    @Override
    public void warn(LogMessageImpl msg) {
        this.stream.println(msg.format(1));
    }

    @Override
    public void error(LogMessageImpl msg) {
        this.stream.println(msg.format(1));
    }

    @Override
    public void debug(LogMessageImpl msg) {
        this.stream.println(msg.format(1));
    }

    @Override
    public OutputStream out() {
        return this.stream;
    }
}