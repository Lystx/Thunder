
package org.gravel.elements.gui.login;

import io.vson.elements.object.VsonObject;
import lombok.Getter;
import lombok.Setter;
import org.gravel.GravelClient;
import org.gravel.elements.gui.screen.MainGUi;
import org.gravel.library.GravelAPI;
import org.gravel.library.manager.account.Account;
import org.gravel.library.manager.networking.packets.result.PacketQueryLogin;
import org.gravel.library.manager.user.GravelUser;
import org.gravel.library.manager.user.UserStatus;
import org.gravel.library.utils.Utils;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.UUID;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

@Getter
public class LoginGui extends JFrame implements ActionListener{

    private JTextField textField1;
    private JLabel label1;
    private JLabel label2;
    private JCheckBox checkBox1;
    private JLabel label4;
    private JPasswordField passwordField1;
    private JButton button1;
    private JButton button2;
    private JPanel vSpacer1;

    private GravelClient gravelClient;
    private boolean registered;

    private boolean allowLogin;

    public LoginGui(boolean registered, GravelClient gravelClient) {
        this(registered, gravelClient, true);
    }

    public LoginGui(boolean registered, GravelClient gravelClient, boolean allowLogin) {
        this.allowLogin = allowLogin;
        final VsonObject vsonObject = gravelClient.getFileManager().getConfig();
        final VsonObject config = gravelClient.getFileManager().getConfig().get("stayLoggedIn").asVsonObject();
        this.registered = registered;
        this.gravelClient = gravelClient;

        this.textField1 = new JTextField();
        this.label1 = new JLabel();
        this.label2 = new JLabel();
        this.checkBox1 = new JCheckBox();
        this.label4 = new JLabel();
        this.passwordField1 = new JPasswordField();
        this.button1 = new JButton();
        this.button2 = new JButton();
        this.vSpacer1 = new JPanel(null);

        this.setTitle("Gravel | Login ");
        this.setFont(new Font(Font.DIALOG, Font.PLAIN, 15));
        Container contentPane = getContentPane();
        contentPane.setLayout(null);
        contentPane.add(textField1);
        this.textField1.setBounds(25, 105, 215, 20);

        this.label1.setText(registered ? "Enter your Username :" : "Choose a Username :");
        contentPane.add(label1);
        this.label1.setBounds(25, 80, 170, 20);

        this.label2.setText(registered ? "Enter your password :" : "Choose a password :");
        contentPane.add(label2);
        this.label2.setBounds(25, 140, 175, label2.getPreferredSize().height);

        this.checkBox1.setText("Remember my Details");
        this.checkBox1.setSelected(config.getBoolean("enabled"));
        this.checkBox1.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                config.append("enabled", checkBox1.isSelected());
                vsonObject.append("stayLoggedIn", config);
                vsonObject.save();
            }
        });
        contentPane.add(checkBox1);
        this.checkBox1.setBounds(new Rectangle(new Point(120, 205), checkBox1.getPreferredSize()));

        this.label4.setText("Gravel Messenger");
        this.label4.setFont(label4.getFont().deriveFont(Font.BOLD|Font.ITALIC, label4.getFont().getSize() + 14f));
        contentPane.add(label4);
        this.label4.setBounds(80, 25, 220, 35);
        contentPane.add(passwordField1);
        this.passwordField1.setBounds(25, 165, 215, 20);
        this.passwordField1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LoginGui.this.actionPerformed(new ActionEvent(this, 1, null));
            }
        });
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                gravelClient.shutdown();
            }
        });

        this.button1.setText("Login");
        contentPane.add(button1);
        this.button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LoginGui.this.actionPerformed(new ActionEvent(this, 1, null));
            }
        });
        this.button1.setBounds(275, 105, 70, button1.getPreferredSize().height);

        this.button2.setText("Cancel");
        this.button2.addActionListener(e -> gravelClient.shutdown());
        contentPane.add(button2);
        this.button2.setBounds(new Rectangle(new Point(275, 165), button2.getPreferredSize()));
        contentPane.add(vSpacer1);
        this.vSpacer1.setBounds(0, 220, 395, 30);

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
        if (config.getBoolean("enabled")) {

            this.textField1.setText(config.getString("username"));
            this.passwordField1.setText(config.getString("password"));

            if (this.allowLogin) {
                this.textField1.setEditable(false);
                this.passwordField1.setEditable(false);

                GravelAPI.getInstance().schedule(() -> {
                    LoginGui.this.actionPerformed(new ActionEvent(this, 1, null));
                }, 7L);
            }

        }
    }

    public void logIn(String userName, String ip, String password, String pw2) {
        GravelAPI.getInstance().sendQuery(new PacketQueryLogin(userName, password, pw2, ip)).onResultSet(vsonObjectResult -> {
            final VsonObject result = vsonObjectResult.getResult();
            System.out.println(result);
            boolean allow = result.getBoolean("allow");
            String message = result.getString("message");
            if (pw2 != null) {
                this.gravelClient.getFileManager().getConfig().append("registered", allow).save();
            }

            if (this.checkBox1.isSelected()) {
                final VsonObject config = this.gravelClient.getFileManager().getConfig();
                config.append("stayLoggedIn", new VsonObject().append("username", userName).append("enabled", true).append("password", password));
                config.save();
            }

            if (message.contains("created")) {
                GravelAPI.getInstance().schedule(() -> System.exit(0), 20L);
                return;
            }
            if (allow && message.equals("Login worked!")) {
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
                    new MainGUi(gravelClient);
                }, 20L);
                return;
            }
            JOptionPane.showMessageDialog(null, message, allow ? "Success" : "Failed",  JOptionPane.INFORMATION_MESSAGE);
        });

    }


    @Override
    public void actionPerformed(ActionEvent e) {
        String userName = textField1.getText();
        String password = passwordField1.getText();
        String ip = "127.0.0.1";
        String pw2 = (!this.registered ? password : null);
        this.logIn(userName, ip, password, pw2);
    }
}
