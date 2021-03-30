package org.gravel.elements.login;

import com.formdev.flatlaf.FlatDarkLaf;
import io.vson.elements.object.VsonObject;
import org.gravel.GravelClient;
import org.gravel.elements.gui.Gui;
import org.gravel.library.GravelAPI;
import org.gravel.library.manager.account.Account;
import org.gravel.library.manager.networking.packets.result.PacketQueryLogin;
import org.gravel.library.manager.user.GravelUser;
import org.gravel.library.manager.user.UserStatus;
import org.gravel.library.utils.Utils;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import javax.swing.*;

public class LoginGui extends JFrame implements ActionListener {

    JPanel panel;
    JLabel user_label, password_label, password_label_repeat, message;
    JTextField userName_text;
    JPasswordField password_text, password_text_repeat;
    JButton submit;

    private final GravelClient gravelClient;

    public LoginGui(boolean registered, GravelClient gravelClient) {
        this.gravelClient = gravelClient;
        FlatDarkLaf.install();
        user_label = new JLabel();
        user_label.setText("Username :");
        userName_text = new JTextField();

        password_label = new JLabel();
        password_label.setText("Password :");
        password_text = new JPasswordField();

        if (!registered) {

            password_label_repeat = new JLabel();
            password_label_repeat.setText("Repeat Password :");
            password_text_repeat = new JPasswordField();

        }

        submit = new JButton("Log in");

        if (registered) {
            panel = new JPanel(new GridLayout(3, 1));
        } else {

            panel = new JPanel(new GridLayout(4, 1));
        }

        panel.add(user_label);
        panel.add(userName_text);
        panel.add(password_label);
        panel.add(password_text);

        password_text.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LoginGui.this.actionPerformed(new ActionEvent(this, 1, null));
            }
        });


        if (!registered) {
            panel.add(password_label_repeat);
            panel.add(password_text_repeat);
        }

        message = new JLabel();
        panel.add(message);
        panel.add(submit);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        submit.addActionListener(this);
        this.add(panel, BorderLayout.CENTER);

        /*URL res = getClass().getClassLoader().getResource("icon.png");
        try {
            File file = Paths.get(res.toURI()).toFile();
            String absolutePath = file.getAbsolutePath();

            setIconImage(Toolkit.getDefaultToolkit().getImage(absolutePath));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }*/
        this.setResizable(false);
        this.setTitle("Gravel | " + (registered ? "Logging in..." : "Register an Account!"));
        this.setSize(300, 100);
        this.setBounds(800, 400, 300, 150);
        this.setVisible(true);
    }

    public void info(String title, String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
    }



    @Override
    public void actionPerformed(ActionEvent ae) {
        String userName = userName_text.getText();
        String password = password_text.getText();
        String ip = "127.0.0.1";
        String pw2 = (this.password_text_repeat == null ? null : this.password_text_repeat.getText());

        GravelAPI.getInstance().sendQuery(new PacketQueryLogin(userName, password, pw2, ip)).onResultSet(vsonObjectResult -> {
            final VsonObject result = vsonObjectResult.getResult();
            boolean allow = result.getBoolean("allow");
            String message = result.getString("message");
            if (pw2 != null) {
                this.gravelClient.getFileManager().getConfig().append("registered", allow).save();
            }

            if (message.contains("created")) {
                this.message.setText("Success! Stopping....");
                GravelAPI.getInstance().schedule(() -> System.exit(0), 20L);
                return;
            }
            if (allow && message.equals("Login worked!")) {
                this.message.setText("Welcome " + userName);
                VsonObject user = result.getVson("user");

                this.gravelClient.setUser(new GravelUser(
                        user.getObject("account", Account.class),
                        VsonObject.encode(user.getVson("settings")),
                        Utils.fromUUIDS(user.getList("friends", UUID.class)),
                        Utils.fromUUIDS(user.getList("muted", UUID.class)),
                        Utils.fromUUIDS(user.getList("requests", UUID.class)),
                        UserStatus.valueOf(user.getString("status"))));
                GravelAPI.getInstance().schedule(() -> {
                    setVisible(false);
                    dispose();
                    new Gui(gravelClient);
                }, 20L);
                return;
            }
            this.info(allow ? "Success" : "Failed", message);
        });

    }

}
