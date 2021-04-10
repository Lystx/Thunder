
package org.gravel.elements.gui.screen;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import lombok.Getter;
import lombok.Setter;
import org.gravel.GravelClient;
import org.gravel.elements.gui.login.LoginGui;
import org.gravel.elements.gui.main.listener.GuiKeyListener;
import org.gravel.elements.newGui.FriendsGui;
import org.gravel.library.GravelAPI;
import org.gravel.library.manager.chatting.Chat;
import org.gravel.library.manager.networking.packets.out.PacketOutUpdatePlayer;
import org.gravel.library.manager.networking.packets.result.PacketQueryChangeTheme;
import org.gravel.library.manager.networking.packets.result.PacketQueryGetChats;
import org.gravel.library.manager.user.GravelUser;
import org.gravel.library.manager.user.UserStatus;
import org.gravel.library.utils.Utils;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

@Getter @Setter
public class MainGUi extends JFrame {

    public static DefaultListModel<Chat> listModel = new DefaultListModel<>();
    private final JScrollPane scrollPane1;
    private final JScrollPane scrollPane2;
    private final JList<Chat> list1;
    private final JTextArea textField1;
    private final JToolBar toolBar1;
    private final JButton button1;
    private final JButton button2;
    private final JButton button3;
    private final JButton button4;
    private final JButton button5;
    private final JButton buttonLogOut;
    private final JTextField messageInput;
    private final JButton button6;
    public static JTextPane textField3;
    private final JLabel label1;
    private final JPanel vSpacer1;

    public static JLabel playerLabel;

    private List<String> history;
    private int currenthistory;

    private GravelClient gravelClient;


    public MainGUi(GravelClient gravelClient) {
        this.gravelClient = gravelClient;
        this.history = new LinkedList<>();
        this.currenthistory = 0;

        this.scrollPane1 = new JScrollPane();
        this.scrollPane2 = new JScrollPane();
        this.list1 = new JList<>(listModel);
        this.textField1 = new JTextArea();
        this.toolBar1 = new JToolBar();
        this.button1 = new JButton();
        this.button2 = new JButton();
        this.button3 = new JButton();
        this.button4 = new JButton();
        this.button5 = new JButton();
        this.buttonLogOut = new JButton();
        this.messageInput = new JTextField();

        this.button6 = new JButton();
        textField3 = new JTextPane();
        this.label1 = new JLabel();
        this.playerLabel = new JLabel();
        this.vSpacer1 = new JPanel(null);

        List<Chat> chats = GravelAPI.getInstance().sendQuery(new PacketQueryGetChats(gravelClient.getUser())).getResult();

        gravelClient.setChats(chats);

        for (Chat chat : chats) {
            listModel.addElement(chat);
            /*if (chat.isGroup()) {
            } else {
                listModel.addElement("[Chat] " + chat.getOtherMember(gravelClient.getUser()).getAccount().getName());
            }*/
        }
        list1.addListSelectionListener(e -> {
            Chat s = list1.getSelectedValue();

            GravelAPI.getInstance().updateChat(s, playerLabel, textField3, gravelClient.getUser());


        });

        this.setTitle("Gravel | Messenger");
        Container contentPane = getContentPane();
        contentPane.setLayout(null);

        scrollPane1.setViewportView(list1);
        contentPane.add(scrollPane1);
        scrollPane1.setBounds(5, 45, 160, 510);
        contentPane.add(textField1);

        textField1.setBounds(5, 20, 160, textField1.getPreferredSize().height);


        textField1.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == 10) {
                    if (textField1.getText().trim().isEmpty()) {
                        return;
                    }
                    Chat chat = gravelClient.getChats().stream().filter(c -> c.getName().contains(textField1.getText())).findFirst().orElse(null);
                    if (chat == null) {
                        Utils.showDialog("Gravel | Error", "Couldn't find any chat!", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    GravelAPI.getInstance().updateChat(chat, playerLabel, textField3, gravelClient.getUser());
                    list1.setSelectedValue(textField1.getText(), true);
                }
            }
        });

        {
            toolBar1.setFloatable(false);

            button1.setText("Log");
            button1.addActionListener(e -> {
                list1.clearSelection();
                GravelAPI.getInstance().updateChat(null, playerLabel, textField3, gravelClient.getUser());
            });
            toolBar1.add(button1);

            button2.setText("Friends");
            button2.addActionListener(e -> {
                list1.clearSelection();
                new FriendsGui(gravelClient);
            });
            toolBar1.add(button2);

            button3.setText("Settings");
            button3.addActionListener(e -> {
                list1.clearSelection();
            });
            toolBar1.add(button3);

            button4.setText("Information");
            button4.addActionListener(e -> {

                list1.clearSelection();
            });
            toolBar1.add(button4);

            button5.setText("Switch theme");
            button5.addActionListener(e -> {
                list1.clearSelection();
                GravelAPI.getInstance().sendQuery(new PacketQueryChangeTheme(gravelClient.getUser())).onResultSet(booleanResult -> {
                    final Boolean result = booleanResult.getResult();
                    if (result) {
                        FlatDarkLaf.install();
                    } else {
                        FlatLightLaf.install();
                    }
                    SwingUtilities.updateComponentTreeUI(this);
                });
            });
            toolBar1.add(button5);

            buttonLogOut.setText("Log out");
            buttonLogOut.addActionListener(e -> {

                list1.clearSelection();
                final int i = JOptionPane.showConfirmDialog(null, "Do you want to return to LogIn-Screen?", "Gravel | logOut", JOptionPane.YES_NO_OPTION);
                switch (i) {
                    case 0:
                        final GravelUser user = gravelClient.getUser();
                        user.setStatus(UserStatus.OFFLINE);
                        GravelAPI.getInstance().sendPacket(new PacketOutUpdatePlayer(user));
                        new LoginGui(true, gravelClient, false);
                        setVisible(false);
                        dispose();
                        break;
                    case 1:
                        break;
                }
            });
            toolBar1.add(buttonLogOut);
        }
        contentPane.add(toolBar1);
        toolBar1.setBounds(165, 15, 615, toolBar1.getPreferredSize().height);
        contentPane.add(messageInput);
        messageInput.addKeyListener(new GuiKeyListener(this));
        messageInput.setBounds(175, 520, 960, 35);

        button6.setText("\ud83d\ude02");
        contentPane.add(button6);
        button6.setBounds(1135, 520, 70, 35);

        textField3.setEditable(false);
        contentPane.add(textField3);
        textField3.setBounds(175, 45, 1025, 465);
        {
            scrollPane2.setViewportView(textField3);
        }
        contentPane.add(scrollPane2);
        scrollPane2.setBounds(175, 45, 960 + 67, 470);

        label1.setText("Search for a conversation");
        contentPane.add(label1);
        label1.setBounds(10, 0, 150, label1.getPreferredSize().height);
        contentPane.add(vSpacer1);
        vSpacer1.setBounds(0, 560, 1205, 5);

        playerLabel.setText("You are in no chat!");
        contentPane.add(playerLabel);
        playerLabel.setBounds(1000, 5, 150, playerLabel.getPreferredSize().height);

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
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        GravelAPI.getInstance().updateChat(null, playerLabel, textField3, gravelClient.getUser());
    }

}
