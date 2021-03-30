package org.gravel.library.manager.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.gravel.library.manager.account.Account;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Getter @AllArgsConstructor @Setter
public class GravelUser implements Serializable {

    private final Account account;
    private final Map<String, Object> settings;
    private final List<GravelUser> friends;
    private final List<GravelUser> muted;
    private final List<GravelUser> requests;
    private UserStatus status;

}
