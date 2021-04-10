
package org.gravel.elements.newGui;

import io.vson.elements.object.VsonObject;
import lombok.Getter;
import lombok.SneakyThrows;
import org.gravel.GravelClient;
import org.gravel.elements.gui.screen.MainGUi;
import org.gravel.library.GravelAPI;
import org.gravel.library.manager.account.Account;
import org.gravel.library.manager.chatting.Chat;
import org.gravel.library.manager.networking.packets.Result;
import org.gravel.library.manager.networking.packets.result.*;
import org.gravel.library.manager.user.GravelUser;
import org.gravel.library.utils.Utils;

import java.awt.*;
import java.util.Arrays;
import java.util.function.Consumer;
import javax.swing.*;

@Getter
public class FriendsGui extends JFrame {

    private final JScrollPane scrollPane1;
    private final JList<String> list1;
    private final JButton button1;
    private final JLabel label1;
    private final JButton button2;
    private final JButton button3;
    private final JButton button4;
    private final JButton button5;
    private final JLabel label3;

    private GravelUser currentUser;

    private String tab;

    @SneakyThrows
    public FriendsGui(GravelClient gravelClient) {
        GravelUser user = gravelClient.getUser();

        for (GravelUser gravelUser : GravelAPI.getInstance().getUserManager().getUsers()) {
            if (gravelUser.getAccount().getName().equalsIgnoreCase(user.getAccount().getName())) {
                user = gravelUser;
                break;
            }
        }
        gravelClient.setUser(user);

        this.tab = "FRIENDS";

        DefaultListModel<String> model = new DefaultListModel<>();
        this.scrollPane1 = new JScrollPane();
        this.list1 = new JList<>(model);
        this.button1 = new JButton();
        this.label1 = new JLabel();
        this.button2 = new JButton();
        this.button3 = new JButton();
        this.button4 = new JButton();
        this.button5 = new JButton();
        this.currentUser = null;
        this.label3 = Utils.fromURL("https://minotar.net/helm/MHF_QUESTION/100");

        this.setTitle("Gravel | Friends Menu");
        Container contentPane = getContentPane();
        contentPane.setLayout(null);

        scrollPane1.setViewportView(list1);

        contentPane.add(scrollPane1);
        scrollPane1.setBounds(0, 22, 140, 290);

        button1.setText("Remove Friend");
        contentPane.add(button1);
        button1.addActionListener(e -> {
            if (currentUser == null) {
                Utils.showDialog("Gravel | Friends", "You have no Friend selected!", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (tab.equalsIgnoreCase("FRIENDS")) {
                GravelAPI.getInstance().sendQuery(new PacketQueryFriendRemove(gravelClient.getUser(), currentUser));
            } else {
                GravelAPI.getInstance().sendQuery(new PacketQueryFriendAccept(gravelClient.getUser(), currentUser)).onResultSet(new Consumer<Result<Boolean>>() {
                    @Override
                    public void accept(Result<Boolean> booleanResult) {
                        Chat chat = GravelAPI.getInstance().sendQuery(new PacketQueryGetChat("create_one", gravelClient.getUser().getAccount(), currentUser.getAccount())).getResult();
                        gravelClient.getChats().add(chat);
                        MainGUi.listModel.addElement(chat);
                    }
                });
            }
            model.removeElement(currentUser.getAccount().getName());
            currentUser = null;
            label1.setText("Nobody selected!");
            label3.setIcon(Utils.getImageIconFromUrl("https://minotar.net/helm/MHF_QUESTION/100"));
        });
        button1.setBounds(145, 240, 115, 55);

        label1.setText("No Friend Selected!");
        label1.setFont(label1.getFont().deriveFont(label1.getFont().getSize() + 15f));
        contentPane.add(label1);
        label1.setBounds(225, 30, 180, 55);

        if (currentUser != null && user.hasMuted(currentUser)) {
            button2.setText("Unmute Friend");
        } else {
            button2.setText("Mute Friend");
        }
        button2.addActionListener(e -> {
            if (currentUser == null) {
                Utils.showDialog("Gravel | Mute Friend", "You have no Friend selected!", JOptionPane.ERROR_MESSAGE);
                return;
            }
            final Boolean result = GravelAPI.getInstance().sendQuery(new PacketQueryMutePlayer(gravelClient.getUser(), currentUser)).getResult();
            button2.setText((result ? "Unmute" : "Mute") + " Friend");
        });
        contentPane.add(button2);
        button2.setBounds(265, 240, 130, 55);

        button3.setText("Placeholder");
        button3.addActionListener(e -> {
            if (tab.equalsIgnoreCase("REQUESTS")) {
                GravelAPI.getInstance().sendQuery(new PacketQueryFriendDeny(gravelClient.getUser(), currentUser));
            } else {
                //LOL
            }
            model.removeElement(currentUser.getAccount().getName());
            currentUser = null;
            label1.setText("Nobody selected!");
            label3.setIcon(Utils.getImageIconFromUrl("https://minotar.net/helm/MHF_QUESTION/100"));
        });
        contentPane.add(button3);
        button3.setBounds(405, 240, 125, 55);

        button4.setText("Add");
        button4.setToolTipText("Add a new Friend");
        GravelUser finalUser1 = user;
        button4.addActionListener(e -> {
            String s = JOptionPane.showInputDialog(null, "Please enter the name of the User you want to add", "Gravel | Add a Friend", JOptionPane.QUESTION_MESSAGE);
            final GravelUser user1 = GravelAPI.getInstance().getUserManager().getUser(s);
            if (user1 == null) {
                JOptionPane.showMessageDialog(null, "There is no player registered with that name!", "Gravel | Add Friend", JOptionPane.ERROR_MESSAGE);
                return;
            }
            final VsonObject result = GravelAPI.getInstance().sendQuery(new PacketQueryFriendRequest(finalUser1, user1)).getResult();

            final boolean allow = result.getBoolean("allow");
            final String message = result.getString("message");

            JOptionPane.showMessageDialog(null, message, "Gravel | " + (allow ? "Success" : "Failed"), JOptionPane.INFORMATION_MESSAGE);
        });
        contentPane.add(button4);
        button4.setBounds(500, 0, 70, button4.getPreferredSize().height);

        button5.setText("Requests");
        button5.addActionListener(e -> {
            model.clear();
            if (tab.equalsIgnoreCase("FRIENDS")) {
                tab = "Requests";
                for (Account request : gravelClient.getUser().getRequests()) {
                    model.addElement(request.getName());
                }
                button1.setText("Accept");
                button3.setText("Decline");
                button2.setVisible(false);
            } else {
                tab = "Friends";
                for (Account friend : gravelClient.getUser().getFriends()) {
                    model.addElement(friend.getName());
                }
                if (currentUser != null && gravelClient.getUser().hasMuted(currentUser)) {
                    button2.setText("Unmute Friend");
                } else {
                    button2.setText("Mute Friend");
                }
                button1.setText("Remove Friend");
                button3.setText("Placeholder");
                button2.setVisible(true);
            }
            button5.setText(tab);
            setTitle("Gravel | " + tab);
            currentUser = null;
            label1.setText("Nobody selected!");
            label3.setIcon(Utils.getImageIconFromUrl("https://minotar.net/helm/MHF_QUESTION/100"));
        });
        contentPane.add(button5);
        button5.setBounds(0, 0, 140, button4.getPreferredSize().height);

        contentPane.add(label3);
        label3.setBounds(235, 95, 150, 100);
        label1.setText("Nobody selected!");

        for (Account friend : user.getFriends()) {
            model.addElement(friend.getName());
        }

        list1.addListSelectionListener(e -> {
            String stringUser = list1.getSelectedValue();
            final GravelUser user1 = GravelAPI.getInstance().getUserManager().getUser(stringUser);
            if (user1 == null) {
                return;
            }
            currentUser = user1;
            label1.setText(currentUser.getAccount().getName() +  " [" + currentUser.getStatus() + "]");
            label3.setIcon(Utils.getImageIconFromUrl("https://minotar.net/helm/" + currentUser.getAccount().getName() + "/100"));
            if (tab.equalsIgnoreCase("FRIENDS")) {
                if (currentUser != null && gravelClient.getUser().hasMuted(currentUser)) {
                    button2.setText("Unmute Friend");
                } else {
                    button2.setText("Mute Friend");
                }
            }


            //TODO: RENDER IMAGE OF USER
        });

        {
            Dimension preferredSize = new Dimension();
            for(int i = 0; i < contentPane.getComponentCount(); i++) {
                Rectangle bounds = contentPane.getComponent(i).getBounds();
                preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
            }
            Insets insets = contentPane.getInsets();
            preferredSize.width += insets.right;
            preferredSize.height += insets.bottom;
            contentPane.setMinimumSize(preferredSize);
            contentPane.setPreferredSize(preferredSize);
        }
        this.pack();
        this.setLocationRelativeTo(this.getOwner());
        this.setVisible(true);
        this.setResizable(false);
    }




}
