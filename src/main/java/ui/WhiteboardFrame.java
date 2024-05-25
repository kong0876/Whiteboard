package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

        // 메뉴 추가
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("파일");

        JMenuItem saveMenuItem = new JMenuItem("저장");
        saveMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int option = fileChooser.showSaveDialog(WhiteboardFrame.this);
                if (option == JFileChooser.APPROVE_OPTION) {
                    String filename = fileChooser.getSelectedFile().getPath();
                    whiteboardPanel.saveShapes(filename);
                }
            }
        });

        JMenuItem loadMenuItem = new JMenuItem("불러오기");
        loadMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int option = fileChooser.showOpenDialog(WhiteboardFrame.this);
                if (option == JFileChooser.APPROVE_OPTION) {
                    String filename = fileChooser.getSelectedFile().getPath();
                    whiteboardPanel.loadShapes(filename);
                }
            }
        });

        fileMenu.add(saveMenuItem);
        fileMenu.add(loadMenuItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
    }

    public WhiteboardPanel getWhiteboardPanel() {
        return whiteboardPanel;
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
