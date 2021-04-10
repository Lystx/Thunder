package net.hytora.cloud;

import net.hytora.library.CloudAPI;
import net.hytora.library.manager.networking.netty.ConnectionServer;

public class CloudSystem {

    public CloudSystem() {
        CloudAPI.getInstance().setNettyConnection(new ConnectionServer("127.0.0.1", 1758));

    }
}
