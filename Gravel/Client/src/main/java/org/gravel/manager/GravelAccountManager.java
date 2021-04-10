package org.gravel.manager;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.gravel.GravelClient;
import org.gravel.library.manager.account.Account;
import org.gravel.library.manager.account.AccountManager;

import java.util.List;
import java.util.UUID;

@Getter @RequiredArgsConstructor
@Setter
public class GravelAccountManager implements AccountManager {

    private final GravelClient gravelClient;

    private List<Account> accounts;

    @Override
    public Account getAccount(String name) {
        return this.accounts.stream().filter(account -> account.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    @Override
    public Account getAccount(UUID uniqueId) {
        return this.accounts.stream().filter(account -> account.getUniqueId() == uniqueId).findFirst().orElse(null);
    }

    @Override
    public void addAcount(Account account) {
        throw new UnsupportedOperationException("Not Supported on GravelClient");
    }
}
