package org.gravel.elements.gui.listener;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.gravel.elements.gui.Gui;
import org.gravel.library.GravelAPI;
import org.gravel.library.manager.chatting.Chat;
import org.gravel.library.manager.networking.packets.result.PacketQueryGetChats;
import org.gravel.library.manager.user.GravelUser;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

@AllArgsConstructor @Getter
public class GuiFriendsMouseListener extends MouseAdapter {

    private final Gui gui;

    @Override
    public void mouseReleased(MouseEvent e) {

        String player = JOptionPane.showInputDialog(this.gui, "Please enter a name", "Manage a friend", JOptionPane.QUESTION_MESSAGE);

        GravelUser gravelUser = GravelAPI.getInstance().getUserManager().getUser(player);
        if (gravelUser == null) {
            JOptionPane.showMessageDialog(this.gui, "There is no User with the name " + player + " registered!", "Manage a friend", JOptionPane.ERROR_MESSAGE);
            return;
        }


        JFrame frame = new JFrame("Friends | " + gravelUser.getAccount().getName());
        JButton addButton = new JButton("Add");
        JButton removeButton = new JButton("Remove");
        JButton muteButton = new JButton("Mute");

        addButton.setBounds(25,5,75,20);
        removeButton.setBounds(120,5,75,20);
        muteButton.setBounds(220,5,75,20);

        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(false);
                frame.dispose();
            }
        });

        frame.add(addButton);
        frame.add(removeButton);
        frame.add(muteButton);
        frame.setLayout(null);
        frame.setBounds(700, 400, 300, 75);
        frame.setVisible(true);

    }

}
