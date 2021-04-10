package net.hytora.library;

import lombok.Getter;
import lombok.Setter;
import net.hytora.library.manager.networking.netty.NettyConnection;
import net.hytora.library.manager.permission.PermissionPool;
import net.hytora.library.manager.player.PlayerPool;
import net.hytora.library.manager.service.GroupPool;
import net.hytora.library.manager.service.ServicePool;

@Getter @Setter
public class CloudAPI {

    private static CloudAPI instance;

    private PlayerPool playerPool;
    private GroupPool groupPool;
    private ServicePool servicePool;
    private PermissionPool permissionPool;

    private NettyConnection nettyConnection;

    public CloudAPI() {

    }


    public static CloudAPI getInstance() {
        if (instance == null) {
            instance = new CloudAPI();
        }
        return instance;
    }
}
