

package io.thunder.manager.factory;

import io.thunder.connection.ThunderClient;

public interface ClientFactory {

    /**
     * Returns the current {@link ThunderClient}
     * @return client
     */
    ThunderClient getClient();
}
