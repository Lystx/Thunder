package org.gravel.manager;

import lombok.Getter;
import lombok.Setter;
import org.gravel.library.manager.user.GravelUser;
import org.gravel.library.manager.user.UserManager;

import java.io.File;
import java.util.List;
import java.util.UUID;

@Getter @Setter
public class GravelUserManager implements UserManager {

    private List<GravelUser> users;

    @Override
    public GravelUser getUser(String name) {
        return this.getUsers().stream().filter(gravelUser -> gravelUser.getAccount().getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    @Override
    public GravelUser getUser(UUID uniqueId) {
        return this.getUsers().stream().filter(gravelUser -> gravelUser.getAccount().getUniqueId().equals(uniqueId)).findFirst().orElse(null);
    }
    @Override
    public void addUser(GravelUser gravelUser) {
        throw new UnsupportedOperationException("Not supported on GravelClient");
    }

    @Override
    public void update(GravelUser gravelUser) {
        throw new UnsupportedOperationException("Not supported on GravelClient");
    }

}
