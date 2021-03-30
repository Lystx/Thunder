package org.gravel.elements.gui.listener;

import com.formdev.flatlaf.FlatDarkLaf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.gravel.GravelClient;
import org.gravel.elements.gui.Gui;
import org.gravel.library.GravelAPI;
import org.gravel.library.manager.chatting.Chat;
import org.gravel.library.manager.chatting.ChatMessage;
import org.gravel.library.manager.networking.packets.result.PacketQueryGetChats;
import org.gravel.library.manager.user.GravelUser;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;
import java.util.List;

@AllArgsConstructor @Getter
public class GuiMouseListener extends MouseAdapter {

    private final Gui gui;

    @Override
    public void mouseReleased(MouseEvent e) {

        GravelUser current = this.gui.getGravelClient().getUser();

        final List<Chat> result = GravelAPI.getInstance().sendQuery(new PacketQueryGetChats(current)).getResult();
        if (result.isEmpty()) {
            JOptionPane.showMessageDialog(this.gui, "Seems like you aren't in any chats!", "Error | Chat Search", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JFrame f = new JFrame("Gravel | Chat Selector");
        JButton b = new JButton("Select");
        b.setBounds(150,5,75,20);

        JComboBox<String> listBox = new JComboBox<>();
        listBox.addItem("MainMenu");
        for (Chat chat : result) {
            listBox.addItem(chat.getName());
        }
        listBox.setBounds(50, 5,90,20);

        f.add(listBox); f.add(b);
        f.setLayout(null);
        f.setBounds(700, 400, 300, 75);
        f.setVisible(true);
        b.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                f.setVisible(false);
                f.dispose();

                final String itemAt = listBox.getItemAt(listBox.getSelectedIndex());
                if (itemAt.equals("MainMenu")) {
                    GravelAPI.getInstance().updateChat(null, Gui.consoleLabel, Gui.textOutput, gui.getGravelClient().getUser());

                } else {
                    Chat chat = result.stream().filter(c -> c.getName().equalsIgnoreCase(itemAt)).findFirst().orElse(null);

                    GravelAPI.getInstance().updateChat(chat, Gui.consoleLabel, Gui.textOutput, gui.getGravelClient().getUser());
                }

            }
        });



        /*final String play
        er = JOptionPane.showInputDialog(this.gui, "Please enter a name", "Start a Conversation", JOptionPane.QUESTION_MESSAGE);

        GravelUser user = GravelAPI.getInstance().getUserManager().getUser(player);
        if (user == null) {
            JOptionPane.showMessageDialog(this.gui, "There is no player with name " + player + " registered!", "Error | Player Search", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        final List<Chat> result = GravelAPI.getInstance().sendQuery(new PacketQueryGetChats(current)).getResult();
        Chat chat = result.stream().filter(chat1 ->
                chat1.getName().toLowerCase().contains(current.getAccount().getName().toLowerCase()) && chat1.getName().contains(user.getAccount().getName())).findFirst().orElse(null);

        if (chat == null) {

            return;
        }*/
    }

}
