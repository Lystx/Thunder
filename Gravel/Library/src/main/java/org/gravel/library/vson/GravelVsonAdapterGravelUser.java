package org.gravel.library.vson;

import io.vson.VsonValue;
import io.vson.annotation.other.VsonAdapter;
import io.vson.elements.object.VsonObject;
import io.vson.manage.vson.VsonWriter;
import org.gravel.library.GravelAPI;
import org.gravel.library.manager.account.Account;
import org.gravel.library.manager.user.GravelUser;
import org.gravel.library.manager.user.UserStatus;
import org.gravel.library.utils.Utils;

import java.util.UUID;

public class GravelVsonAdapterGravelUser implements VsonAdapter<GravelUser> {

    @Override
    public VsonValue write(GravelUser gravelUser, VsonWriter vsonWriter) {
        VsonObject vsonObject = new VsonObject();
        vsonObject.append("account", gravelUser.getAccount().getUniqueId());
        vsonObject.append("settings", gravelUser.getSettings());
        vsonObject.append("friends", Utils.fromList(gravelUser.getFriends()));
        vsonObject.append("muted", Utils.fromList(gravelUser.getMuted()));
        vsonObject.append("requests", Utils.fromList(gravelUser.getRequests()));
        vsonObject.append("status", gravelUser.getStatus().name());
        return vsonObject;
    }

    @Override
    public GravelUser read(VsonValue vsonValue) {
        VsonObject vsonObject = (VsonObject) vsonValue;
        Account account = GravelAPI.getInstance().getAccountManager().getAccount(UUID.fromString(vsonObject.getString("account")));
        return new GravelUser(
                account,
                VsonObject.encode(vsonObject.getVson("settings")),
                Utils.fromUUIDS(vsonObject.getList("friends", UUID.class)),
                Utils.fromUUIDS(vsonObject.getList("muted", UUID.class)),
                Utils.fromUUIDS(vsonObject.getList("requests", UUID.class)),
                UserStatus.valueOf(vsonObject.getString("status")));
    }

    @Override
    public Class<GravelUser> getTypeClass() {
        return GravelUser.class;
    }
}
