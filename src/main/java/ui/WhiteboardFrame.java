package ui;

import javax.swing.*;
import java.awt.*;

public class WhiteboardFrame extends JFrame {
    private WhiteboardPanel whiteboardPanel;

    public WhiteboardFrame() {
        setTitle("공유 화이트보드");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        whiteboardPanel = new WhiteboardPanel();
        add(whiteboardPanel, BorderLayout.CENTER);

        WhiteboardToolbar toolbar = new WhiteboardToolbar(whiteboardPanel);
        add(toolbar, BorderLayout.NORTH);

        WhiteboardProperties properties = new WhiteboardProperties(whiteboardPanel);
        add(properties, BorderLayout.SOUTH);
    }
}