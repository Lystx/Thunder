package de.lystx.messenger.manager.account;

import de.lystx.messenger.MessageAPI;
import de.lystx.messenger.util.Appender;

public class AccountManager extends Appender<Account> {

    public AccountManager() {
        super(Account.class, "accounts");
        this.access = true;
    }

    @Override
    public void append(String key, Account log) {
        super.append(key, log);
        MessageAPI.getInstance().setAccounts(this.getList());
    }

    @Override
    public void update(String key, Account update) {
        super.update(key, update);
        MessageAPI.getInstance().setAccounts(this.getList());
    }

    @Override
    public void remove(String key) {
        super.remove(key);
        MessageAPI.getInstance().setAccounts(this.getList());
    }

    public Account getAccount(String name) {
        return this.getList().stream().filter(account -> account.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public Account getAccount(int id) {
        return this.getList().stream().filter(account -> account.getId() == id).findFirst().orElse(null);
    }

    public Account getAccountByIP(String ip) {
        return this.getList().stream().filter(account -> account.getIp().equalsIgnoreCase(ip)).findFirst().orElse(null);
    }
}
