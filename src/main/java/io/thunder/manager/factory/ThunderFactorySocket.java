

package io.thunder.manager.factory;

import java.net.Socket;

public interface ThunderFactorySocket {

    /**
     * Returns the Socket for the host and the port
     *
     * @param host the host to connect to
     * @param port the port to connect to
     * @return Socket
     * @throws Exception when something went wrong
     */
    Socket getSocket(String host, Integer port) throws Exception;
}
