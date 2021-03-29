package de.lystx.messenger.manager.setup.impl;

import de.lystx.messenger.manager.setup.AbstractSetup;
import de.lystx.messenger.manager.setup.Setup;
import lombok.Getter;

@Getter
public class AccountSetup extends AbstractSetup<AccountSetup> {

    @Setup(id = 1, question = "What's the name of your Account?")
    private String name;

    @Setup(id = 2, question = "Please enter your password!")
    private String password;

    @Setup(id = 3, question = "Please Re-Enter your password!")
    private String reEnteredPassword;
}
