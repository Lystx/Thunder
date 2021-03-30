package org.gravel.elements.gui;


import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import lombok.Getter;
import lombok.Setter;
import org.gravel.GravelClient;
import org.gravel.elements.gui.listener.GuiCloseListener;
import org.gravel.elements.gui.listener.GuiFriendsMouseListener;
import org.gravel.elements.gui.listener.GuiKeyListener;
import org.gravel.elements.gui.listener.GuiMouseListener;
import org.gravel.library.GravelAPI;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;

 @Setter @Getter
public class Gui extends JFrame {

     public static JPanel contentPane;
     public static JTextField messageInput;
     public static JButton execCommand, friends;
     public static JTextPane textOutput;
     public static JScrollPane scrollPane;
     public static JLabel consoleLabel;
     public static JLabel clientLabel;
     public static JTextPane clientText;
     public static JScrollPane scrollPane_client;
     public static JLabel sysLabel;
     public static JScrollPane scrollPane_sys;
     public static JTextPane sysText;

     private List<String> history;
     private int currenthistory;
     private boolean loadFinish;

     private final GravelClient gravelClient;

     public Gui(GravelClient gravelClient) {
         this.gravelClient = gravelClient;
         if ((Boolean) gravelClient.getUser().getSettings().get("darkMode")) {
             FlatDarkLaf.install();
         } else {
             FlatLightLaf.install();
         }
         Gui frame = new Gui(gravelClient, true);
         frame.setVisible(true);

         Thread t1 = new Thread(() -> new SystemInfo(this).printInfo());
         t1.start();

         loadFinish = true;


     }

     private Gui(GravelClient gravelClient, boolean init) {
         this.gravelClient = gravelClient;
         this.history = new LinkedList<>();
         this.currenthistory = 0;
         this.loadFinish = false;

         setDefaultCloseOperation(DISPOSE_ON_CLOSE);
         this.setTitle("Gravel | Messenger");
         this.addWindowListener(new GuiCloseListener(this));

         this.setBounds(100, 100, 1198, 686);

         this.contentPane = new JPanel();
         this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
         this.setContentPane(contentPane);

         GridBagLayout gbl_contentPane = new GridBagLayout();
         gbl_contentPane.columnWidths = new int[]{243, 10, 36, 111, 0};
         gbl_contentPane.rowHeights = new int[]{0, 160, 0, 10, 33, 33, 0};
         gbl_contentPane.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
         gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
         contentPane.setLayout(gbl_contentPane);

         sysLabel = new JLabel("System Information");
         sysLabel.setFont(new Font("Arial", Font.BOLD, 11));
         GridBagConstraints gbc_sysLabel = new GridBagConstraints();
         gbc_sysLabel.fill = GridBagConstraints.BOTH;
         gbc_sysLabel.insets = new Insets(0, 0, 5, 5);
         gbc_sysLabel.gridx = 0;
         gbc_sysLabel.gridy = 0;
         contentPane.add(sysLabel, gbc_sysLabel);

         consoleLabel = new JLabel("You are in no chat currently!");
         consoleLabel.setFont(new Font("Arial", Font.BOLD, 11));
         GridBagConstraints gbc_consoleLabel = new GridBagConstraints();
         gbc_consoleLabel.anchor = GridBagConstraints.WEST;
         gbc_consoleLabel.insets = new Insets(0, 0, 5, 5);
         gbc_consoleLabel.gridx = 2;
         gbc_consoleLabel.gridy = 0;
         contentPane.add(consoleLabel, gbc_consoleLabel);

         messageInput = new JTextField();
         messageInput.setToolTipText("Enter a Message");
         messageInput.addKeyListener(new GuiKeyListener(this));

         scrollPane_sys = new JScrollPane();
         GridBagConstraints gbc_scrollPane_sys = new GridBagConstraints();
         gbc_scrollPane_sys.insets = new Insets(0, 0, 5, 5);
         gbc_scrollPane_sys.fill = GridBagConstraints.BOTH;
         gbc_scrollPane_sys.gridx = 0;
         gbc_scrollPane_sys.gridy = 1;
         contentPane.add(scrollPane_sys, gbc_scrollPane_sys);

         sysText = new JTextPane();
         sysText.setFont(new Font("Consolas", Font.PLAIN, 12));
         sysText.setEditable(false);
         scrollPane_sys.setViewportView(sysText);

         clientLabel = new JLabel("Online Clients");
         clientLabel.setFont(new Font("Arial", Font.BOLD, 11));
         GridBagConstraints gbc_clientLabel = new GridBagConstraints();
         gbc_clientLabel.anchor = GridBagConstraints.WEST;
         gbc_clientLabel.insets = new Insets(0, 0, 5, 5);
         gbc_clientLabel.gridx = 0;
         gbc_clientLabel.gridy = 3;
         contentPane.add(clientLabel, gbc_clientLabel);

         scrollPane_client = new JScrollPane();
         GridBagConstraints gbc_scrollPane_client = new GridBagConstraints();
         gbc_scrollPane_client.fill = GridBagConstraints.BOTH;
         gbc_scrollPane_client.gridheight = 2;
         gbc_scrollPane_client.insets = new Insets(0, 0, 0, 5);
         gbc_scrollPane_client.gridx = 0;
         gbc_scrollPane_client.gridy = 4;
         contentPane.add(scrollPane_client, gbc_scrollPane_client);

         clientText = new JTextPane();
         scrollPane_client.setViewportView(clientText);
         clientText.setFont(new Font("Consolas", Font.PLAIN, 12));
         clientText.setEditable(false);

         scrollPane = new JScrollPane();
         GridBagConstraints gbc_scrollPane = new GridBagConstraints();
         gbc_scrollPane.gridheight = 4;
         gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
         gbc_scrollPane.gridwidth = 2;
         gbc_scrollPane.fill = GridBagConstraints.BOTH;
         gbc_scrollPane.gridx = 2;
         gbc_scrollPane.gridy = 1;
         contentPane.add(scrollPane, gbc_scrollPane);

         textOutput = new JTextPane();
         scrollPane.setViewportView(textOutput);
         textOutput.setFont(new Font("Consolas", Font.PLAIN, 12));
         textOutput.setEditable(false);

         messageInput.setFont(new Font("Tahoma", Font.PLAIN, 19));
         GridBagConstraints gbc_commandInput = new GridBagConstraints();
         gbc_commandInput.insets = new Insets(0, 0, 0, 5);
         gbc_commandInput.fill = GridBagConstraints.BOTH;
         gbc_commandInput.gridx = 2;
         gbc_commandInput.gridy = 5;
         contentPane.add(messageInput, gbc_commandInput);
         messageInput.setColumns(10);

         execCommand = new JButton("Chats");
         execCommand.setToolTipText("Lists all your chats");
         execCommand.addMouseListener(new GuiMouseListener(this));
         execCommand.setFont(new Font("Tahoma", Font.PLAIN, 19));
         GridBagConstraints gbc_execCommand = new GridBagConstraints();
         gbc_execCommand.fill = GridBagConstraints.BOTH;
         gbc_execCommand.gridx = 3;
         gbc_execCommand.gridy = 5;
         contentPane.add(execCommand, gbc_execCommand);

         friends = new JButton("Friends");
         friends.setToolTipText("Manage all your friends");
         friends.addMouseListener(new GuiFriendsMouseListener(this));
         friends.setFont(new Font("Tahoma", Font.PLAIN, 19));
         GridBagConstraints gbc_friends = new GridBagConstraints();
         gbc_friends.fill = GridBagConstraints.BOTH;
         gbc_friends.gridx = 3;
         gbc_friends.gridy = 6;
         contentPane.add(friends, gbc_friends);


         GravelAPI.getInstance().updateChat(null, consoleLabel, textOutput, this.gravelClient.getUser());
     }

     @Override
     public JPanel getContentPane() {
         return contentPane;
     }

     public JTextField getMessageInput() {
         return messageInput;
     }

     public JTextPane getSysText() {
         return sysText;
     }

     public List<String> getHistory() {
         return history;
     }

     public JTextPane getClientText() {
         return clientText;
     }

     public int getCurrenthistory() {
         return currenthistory;
     }
 }
