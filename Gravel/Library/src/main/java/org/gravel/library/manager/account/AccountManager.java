package org.gravel.library.manager.account;

import java.util.List;
import java.util.UUID;

public interface AccountManager {

    List<Account> getAccounts();

    Account getAccount(String name);


    Account getAccount(UUID uniqueId);

    void addAcount(Account account);

}
