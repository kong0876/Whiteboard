package ui;

import javax.swing.SwingUtilities;
import client.WhiteboardClient;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginDialog loginDialog = new LoginDialog(null);
            loginDialog.setVisible(true);
            String clientId = loginDialog.getClientId();

            if (clientId != null) {
                WhiteboardClient client = new WhiteboardClient(clientId);
                client.connect();

                WhiteboardFrame frame = new WhiteboardFrame(client);
                frame.setVisible(true);
            } else {
                System.exit(0);
            }
        });
    }
}
