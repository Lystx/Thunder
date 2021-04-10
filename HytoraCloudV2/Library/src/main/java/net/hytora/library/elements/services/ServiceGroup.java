package net.hytora.library.elements.services;

import net.hytora.library.elements.base.CloudObject;
import net.hytora.library.elements.enums.ServiceType;
import net.hytora.library.elements.sender.CloudPlayer;

import java.util.List;

public interface ServiceGroup extends CloudObject<ServiceGroup> {

    int getMaxServer();

    int getMinServer();

    int getMemory();

    int getPercentForNewServer();

    String getTemplate();

    ServiceType getServiceType();

    List<CloudPlayer> getOnlinePlayers();

    List<Service> getOnlineServices();
}
