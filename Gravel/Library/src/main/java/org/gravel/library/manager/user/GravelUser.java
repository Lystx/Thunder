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
    private final List<Account> friends;
    private final List<Account> muted;
    private final List<Account> requests;
    private UserStatus status;


    public boolean isFriends(GravelUser user) {
        for (Account gravelUser : this.friends) {
            if (gravelUser.getName().equals(user.getAccount().getName())) {
                return true;
            }
        }
        return false;
    }

    public boolean isMuted(GravelUser user) {
        for (Account gravelUser : this.muted) {
            if (gravelUser.getName().equals(user.getAccount().getName())) {
                return true;
            }
        }
        return false;
    }

    public boolean hasRequest(GravelUser user) {
        for (Account gravelUser : this.requests) {
            if (gravelUser.getName().equals(user.getAccount().getName())) {
                return true;
            }
        }
        return false;
    }

    public boolean hasMuted(GravelUser user) {
        for (Account gravelUser : this.muted) {
            if (gravelUser.getName().equals(user.getAccount().getName())) {
                return true;
            }
        }
        return false;
    }

}
