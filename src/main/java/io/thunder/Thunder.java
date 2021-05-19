package io.thunder;

import io.thunder.connection.base.ThunderClient;
import io.thunder.connection.base.ThunderServer;
import io.thunder.connection.extra.ThunderListener;
import io.thunder.packet.Packet;
import io.thunder.connection.ErrorHandler;
import io.thunder.utils.logger.LogLevel;
import io.thunder.utils.logger.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class is used to create new Instances
 * of {@link ThunderServer}s or {@link ThunderClient}s
 *
 * You can also set the {@link LogLevel} or youse the {@link Thunder#EXECUTOR_SERVICE}
 */
public class Thunder {

    public static final Logger LOGGER = new Logger(); //Custom logger for Thunder
    public static ErrorHandler ERROR_HANDLER = new ErrorHandler() {
        @Override
        public void onError(Exception e) {
            e.printStackTrace();
        }

        @Override
        public void onPacketFailure(Packet packet, String _class, Exception e) {
            Thunder.LOGGER.log(LogLevel.ERROR, "A Packet could not be decoded and was marked as null (Class: " + _class + ")");
            Thunder.LOGGER.log(LogLevel.ERROR, "Exception: ");
            if (Thunder.LOGGER.getLogLevel().equals(LogLevel.ERROR) && e != null) {
                onError(e);
            }
        }
    };

    public static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool(); //ExecutorService to make things run async

    /**
     * Sets the level for the Logger
     * so you could disable it or only listen to
     * errors for example if you use {@link LogLevel#ERROR}
     *
     * @param logging the LogLevel
     */
    public synchronized static void setLogging(LogLevel logging) {
        LOGGER.setLogLevel(logging);
    }

    public static void addHandler(ErrorHandler errorHandler) {
        ERROR_HANDLER = errorHandler;
    }

    /**
     * Creates a new {@link ThunderClient}
     *
     * @return the created Client
     */
    public synchronized static ThunderClient createClient() {
        return createClient(null);
    }

    /**
     * Creates a new {@link ThunderClient}
     * and automatically sets the {@link ThunderListener}
     * for it but it not connects the Client
     *
     * @param thunderListener
     * @return the created Client
     */
    public synchronized static ThunderClient createClient(ThunderListener thunderListener) {
        ThunderClient thunderClient = ThunderClient.newInstance();
        thunderClient.addSessionListener(thunderListener == null ? ThunderListener.empty() : thunderListener);
        return thunderClient;
    }

    /**
     * Creates a {@link ThunderServer} with
     * no Encryption at all it's a "raw" server
     * with no Listener
     *
     * @return created Server
     */
    public synchronized static ThunderServer createServer() {
        return createServer(null);
    }

    /**
     * Creates a {@link ThunderServer} and
     * sets the Listener for it automatically
     *
     * @param thunderListener the Listener
     * @return created Server
     */
    public synchronized static ThunderServer createServer(ThunderListener thunderListener) {
        ThunderServer thunderServer = ThunderServer.newInstance();
        thunderServer.addSessionListener(thunderListener == null ? ThunderListener.empty() : thunderListener);
        return thunderServer;
    }

}
