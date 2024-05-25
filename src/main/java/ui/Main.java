package ui;

import javax.swing.SwingUtilities;
import client.WhiteboardClient;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginDialog loginDialog = new LoginDialog(null);
            loginDialog.setVisible(true);
            String clientId = loginDialog.getClientId();
            String serverAddress = loginDialog.getServerAddress();

            if (clientId != null && serverAddress != null) {
                WhiteboardClient client = new WhiteboardClient(clientId, serverAddress);
                WhiteboardFrame frame = new WhiteboardFrame(client);
                client.setFrame(frame);
                client.connect();

                frame.setVisible(true);
            } else {
                System.exit(0);
            }
        });
    }
}
