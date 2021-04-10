package net.hytora.library.elements.services;

import net.hytora.library.elements.base.CloudObject;
import net.hytora.library.elements.enums.ServiceState;
import net.hytora.library.elements.sender.CloudPlayer;

import java.util.List;

public interface Service extends CloudObject<Service> {

    int getID();

    int getPort();

    ServiceState getState();

    void update();

    void setServiceState(ServiceState state);

    List<CloudPlayer> getOnlinePlayers();

    ServiceGroup getGroup();

    default String getName() {
        return this.getGroup().getName() + "-" + this.getID();
    }

}
