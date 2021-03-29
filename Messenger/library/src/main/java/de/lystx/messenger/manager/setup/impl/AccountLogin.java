package de.lystx.messenger.manager.setup.impl;

import de.lystx.messenger.manager.setup.AbstractSetup;
import de.lystx.messenger.manager.setup.Setup;
import lombok.Getter;

@Getter
public class AccountLogin extends AbstractSetup<AccountLogin> {

    @Setup(id = 1, question = "Name of your Account")
    private String userName;

    @Setup(id = 2, question = "Password of your Account")
    private String password;
}
