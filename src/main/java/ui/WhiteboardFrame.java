package ui;

import javax.swing.*;
import java.awt.*;
import client.WhiteboardClient;

public class WhiteboardFrame extends JFrame {
    private WhiteboardPanel whiteboardPanel;
    private WhiteboardClient client;
    private DefaultListModel<String> clientListModel;
    private JList<String> clientList;

    public WhiteboardFrame(WhiteboardClient client) {
        this.client = client;
        setTitle("공유 화이트보드");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600); // 프레임 크기 조정
        setLocationRelativeTo(null);

        whiteboardPanel = new WhiteboardPanel(client);
        add(whiteboardPanel, BorderLayout.CENTER);

        WhiteboardToolbar toolbar = new WhiteboardToolbar(whiteboardPanel);
        add(toolbar, BorderLayout.NORTH);

        WhiteboardProperties properties = new WhiteboardProperties(whiteboardPanel);
        add(properties, BorderLayout.SOUTH);

        clientListModel = new DefaultListModel<>();
        clientList = new JList<>(clientListModel);
        JScrollPane scrollPane = new JScrollPane(clientList);
        scrollPane.setPreferredSize(new Dimension(200, 0)); // 우측 패널 크기 조정
        add(scrollPane, BorderLayout.EAST);
    }

    public void updateClientList(String[] clients) {
        clientListModel.clear();
        for (String client : clients) {
            clientListModel.addElement(client);
        }
    }

    public void addClient(String clientId) {
        clientListModel.addElement(clientId);
    }

    public void removeClient(String clientId) {
        clientListModel.removeElement(clientId);
    }
}
