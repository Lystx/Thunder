package org.gravel.manager;

import org.gravel.library.GravelAPI;
import org.gravel.library.manager.account.Account;
import org.gravel.library.manager.account.AccountManager;
import org.gravel.library.manager.user.GravelUser;
import org.gravel.library.utils.AppendableMap;
import org.gravel.library.utils.Appender;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GravelAccountManager extends Appender<Account> implements AccountManager {

    public GravelAccountManager(File save) {
        super(Account.class, save);
        this.access = true;
    }

    @Override
    public List<Account> getAccounts() {
        return this.getList();
    }

    @Override
    public Account getAccount(String name) {
        return this.getList().stream().filter(account -> account.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    @Override
    public Account getAccount(UUID uniqueId) {
        return this.getList().stream().filter(account -> account.getUniqueId().equals(uniqueId)).findFirst().orElse(null);
    }

    @Override
    public void addAcount(Account account) {
        this.append(account.getUniqueId().toString(), account);
    }
}
