package ui;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            WhiteboardFrame frame = new WhiteboardFrame();
            frame.setVisible(true);
        });
    }
}
