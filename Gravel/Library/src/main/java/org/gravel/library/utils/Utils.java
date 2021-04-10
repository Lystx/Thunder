package org.gravel.library.utils;

import lombok.SneakyThrows;
import org.gravel.library.GravelAPI;
import org.gravel.library.manager.account.Account;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class Utils {


    public static List<UUID> fromList(List<Account> gravelUsers) {
        List<UUID> list = new LinkedList<>();
        for (Account friend : gravelUsers) {
            list.add(friend.getUniqueId());
        }
        return list;
    }

    public static List<Account> fromUUIDS(List<UUID> uuids) {
        List<Account> list = new LinkedList<>();
        for (UUID friend : uuids) {
            final Account account = GravelAPI.getInstance().getAccountManager().getAccount(friend);
            if (account == null) {
                continue;
            }
            list.add(account);
        }
        return list;
    }

    @SneakyThrows
    public static JLabel fromURL(String url) {
        return new JLabel(getImageIconFromUrl(url));
    }

    public static void showDialog(String title, String content, int message) {
        JOptionPane.showMessageDialog(null, content, title, message);
    }


    @SneakyThrows
    public static ImageIcon getImageIconFromUrl(String url) {
        return
                new ImageIcon(
                        ImageIO.
                                read(
                                        new URL(
                                                url
                                        )
                                )
                );
    }

}
