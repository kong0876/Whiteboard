package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class WhiteboardToolbar extends JToolBar {
    private WhiteboardPanel whiteboardPanel;

    public WhiteboardToolbar(WhiteboardPanel panel) {
        this.whiteboardPanel = panel;

        JToggleButton circleButton = createStyledToggleButton("원", e -> whiteboardPanel.setCurrentAction("원"));
        JToggleButton rectangleButton = createStyledToggleButton("사각형", e -> whiteboardPanel.setCurrentAction("사각형"));
        JToggleButton lineButton = createStyledToggleButton("선", e -> whiteboardPanel.setCurrentAction("선"));
        JToggleButton textButton = createStyledToggleButton("텍스트", e -> whiteboardPanel.setCurrentAction("텍스트"));

        ButtonGroup group = new ButtonGroup();
        group.add(circleButton);
        group.add(rectangleButton);
        group.add(lineButton);
        group.add(textButton);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(circleButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        add(rectangleButton, gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        add(lineButton, gbc);

        gbc.gridx = 3;
        gbc.gridy = 0;
        add(textButton, gbc);
    }

    private JToggleButton createStyledToggleButton(String text, ActionListener actionListener) {
        JToggleButton button = new JToggleButton(text);
        button.setFont(button.getFont().deriveFont(Font.BOLD));
        button.setPreferredSize(new Dimension(120, 40));
        button.addActionListener(actionListener);
        button.addActionListener(e -> updateButtonStyles());

        return button;
    }

    private void updateButtonStyles() {
        for (Component comp : getComponents()) {
            if (comp instanceof JToggleButton) {
                JToggleButton button = (JToggleButton) comp;
                if (button.isSelected()) {
                    button.setBackground(Color.YELLOW);
                } else {
                    button.setBackground(UIManager.getColor("Button.background"));
                }
            }
        }
    }
}
