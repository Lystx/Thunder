package org.gravel.manager;

import org.gravel.library.GravelAPI;
import org.gravel.library.manager.user.GravelUser;
import org.gravel.library.manager.user.UserManager;
import org.gravel.library.utils.Appender;

import java.io.File;
import java.util.List;
import java.util.UUID;

public class GravelUserManager extends Appender<GravelUser> implements UserManager  {

    public GravelUserManager(File save) {
        super(GravelUser.class, save);
        this.access = true;
    }

    @Override
    public List<GravelUser> getUsers() {
        return this.getList();
    }

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
        GravelAPI.getInstance().getAccountManager().addAcount(gravelUser.getAccount());
        this.append(gravelUser.getAccount().getUniqueId().toString(), gravelUser);
    }

    @Override
    public void update(GravelUser gravelUser) {
        this.update(gravelUser.getAccount().getUniqueId().toString(), gravelUser);
    }
}
