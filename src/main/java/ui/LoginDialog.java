package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginDialog extends JDialog {
    private JTextField idField;
    private JButton loginButton;
    private String clientId;

    public LoginDialog(Frame parent) {
        super(parent, "Login", true);
        setLayout(new BorderLayout());

        idField = new JTextField(15);
        loginButton = new JButton("Login");

        JPanel panel = new JPanel();
        panel.add(new JLabel("Client ID:"));
        panel.add(idField);
        panel.add(loginButton);

        add(panel, BorderLayout.CENTER);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clientId = idField.getText();
                if (clientId != null && !clientId.isEmpty()) {
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(LoginDialog.this, "Client ID를 입력하세요.", "Error", JOptionPane.ERROR_MESSAGE);
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
}
