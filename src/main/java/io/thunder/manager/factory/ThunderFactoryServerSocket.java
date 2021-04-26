

package io.thunder.manager.factory;

import java.net.ServerSocket;

public interface ThunderFactoryServerSocket {

    /**
     * Returns a ServerSocket for a given Port
     *
     * @param port the given Port
     * @return Socket of server
     * @throws Exception if something went wrong
     */
    ServerSocket getSocket(Integer port) throws Exception;
}
