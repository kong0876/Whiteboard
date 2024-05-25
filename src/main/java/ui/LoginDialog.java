package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginDialog extends JDialog {
    private JTextField idField;
    private JTextField serverAddressField;
    private JButton loginButton;
    private String clientId;
    private String serverAddress;

    public LoginDialog(Frame parent) {
        super(parent, "로그인", true);
        setLayout(new BorderLayout());

        idField = new JTextField(15);
        serverAddressField = new JTextField(15);
        loginButton = new JButton("확인");

        JPanel panel = new JPanel();
        panel.add(new JLabel("ID:"));
        panel.add(idField);
        panel.add(new JLabel("서버 주소:"));
        panel.add(serverAddressField);
        panel.add(loginButton);

        add(panel, BorderLayout.CENTER);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clientId = idField.getText();
                serverAddress = serverAddressField.getText();
                if (clientId != null && !clientId.isEmpty() && serverAddress != null && !serverAddress.isEmpty()) {
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(LoginDialog.this, "Client ID와 서버 주소를 입력하세요.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        pack();
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    public String getClientId() {
        return clientId;
    }

    public String getServerAddress() {
        return serverAddress;
    }
}
