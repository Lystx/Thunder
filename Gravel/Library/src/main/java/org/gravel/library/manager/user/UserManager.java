package org.gravel.library.manager.user;


import java.util.List;
import java.util.UUID;

public interface UserManager {

    List<GravelUser> getUsers();

    GravelUser getUser(String name);

    GravelUser getUser(UUID uniqueId);

    void addUser(GravelUser gravelUser);

    void update(GravelUser gravelUser);

}
