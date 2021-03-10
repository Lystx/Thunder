
package io.vera.logger;

import javax.annotation.concurrent.Immutable;
import java.time.ZonedDateTime;

@Immutable
public interface LogMessage {

    Logger getLogger();

    String[] getComponents();

    String getMessage();

    ZonedDateTime getTime();
}
