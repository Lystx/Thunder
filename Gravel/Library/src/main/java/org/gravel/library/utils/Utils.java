package org.gravel.library.utils;

import org.gravel.library.GravelAPI;
import org.gravel.library.manager.user.GravelUser;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class Utils {


    public static List<UUID> fromList(List<GravelUser> gravelUsers) {
        List<UUID> list = new LinkedList<>();
        for (GravelUser friend : gravelUsers) {
            list.add(friend.getAccount().getUniqueId());
        }
        return list;
    }

    public static List<GravelUser> fromUUIDS(List<UUID> uuids) {
        List<GravelUser> list = new LinkedList<>();
        for (UUID friend : uuids) {
            list.add(GravelAPI.getInstance().getUserManager().getUser(friend));
        }
        return list;
    }
}
