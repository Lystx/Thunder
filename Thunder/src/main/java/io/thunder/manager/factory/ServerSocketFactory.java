

package io.thunder.manager.factory;

import java.net.ServerSocket;

public interface ServerSocketFactory {

    /**
     * Returns a ServerSocket for a given Port
     *
     * @param port the given Port
     * @return Socket of server
     * @throws Exception if something went wrong
     */
    ServerSocket getServerSocket(int port) throws Exception;
}
