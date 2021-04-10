package io.thunder.manager.utils;

import lombok.Getter;
import lombok.Setter;

/**
 * This class logs all errors / messages
 * or DEBUG Informations for {@link io.thunder.connection.ThunderClient}
 * {@link io.thunder.connection.ThunderServer} and much more
 *
 * If you set the {@link LogLevel} to {@link LogLevel#OFF} nothing
 * will be logged anymore
 */
@Setter @Getter
public class Logger {

    private LogLevel logLevel = LogLevel.ERROR;

    public void log(LogLevel logLevel, String message) {
        if (this.logLevel.equals(LogLevel.ALL)) {
            System.out.println("[Thunder/LOGGING/" + logLevel.name() + "] " + message);
        } else if (this.logLevel.equals(LogLevel.OFF)) {
            //DO NOTHING
        } else {
            if (this.logLevel.equals(logLevel)) {
                System.out.println("[Thunder/LOGGING/" + logLevel.name() + "] " + message);
            }
        }
    }


}
