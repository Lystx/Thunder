

package io.thunder.manager.factory;

import io.thunder.connection.base.ThunderClient;

public interface ThunderFactoryClient {

    /**
     * Returns the current {@link ThunderClient}
     * @return client
     */
    ThunderClient getThunderClient();
}
