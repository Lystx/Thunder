package net.hytora.library.manager.service;

import net.hytora.library.elements.services.Service;
import net.hytora.library.elements.services.ServiceGroup;
import net.hytora.library.manager.Pool;

public interface ServicePool extends Pool<Service> {

    default void startService(ServiceGroup group) {
        this.startService(group, 1);
    }
    void startService(ServiceGroup group, int services);

    void stopService(Service service);

}
