package io.vera.logger;

import java.time.ZonedDateTime;
import javax.annotation.concurrent.Immutable;

@Immutable
public interface LogMessage {
    Logger getLogger();

    String[] getComponents();

    String getMessage();

    ZonedDateTime getTime();
}
