package org.gravel.library.manager.account;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.UUID;

@Getter @AllArgsConstructor
public class Account implements Serializable {

    private final UUID uniqueId;
    private final String name;
    private final String password;
    private final String ipAddress;
    private final long creationDate;

}
