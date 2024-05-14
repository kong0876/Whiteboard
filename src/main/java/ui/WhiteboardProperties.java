package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class WhiteboardProperties extends JPanel {
    private WhiteboardPanel whiteboardPanel;

    public WhiteboardProperties(WhiteboardPanel panel) {
        this.whiteboardPanel = panel;

        // Create toggle buttons
        JToggleButton selectButton = createStyledToggleButton("선택", e -> whiteboardPanel.setCurrentAction("선택"));
        JToggleButton strokeColorButton = createStyledToggleButton("선 색", e -> {
            Color color = JColorChooser.showDialog(this, "선 색 선택", Color.BLACK);
            if (color != null) {
                whiteboardPanel.setCurrentStrokeColor(color);
            }
        });
        JToggleButton fillColorButton = createStyledToggleButton("색 채우기", e -> {
            Color color = JColorChooser.showDialog(this, "색 채우기 선택", Color.BLACK);
            if (color != null) {
                whiteboardPanel.setCurrentFillColor(color);
                whiteboardPanel.setFillShape(true);
                if (whiteboardPanel.getSelectedShape() != null) {
                    whiteboardPanel.getSelectedShape().setFillColor(color);
                    whiteboardPanel.getSelectedShape().setFilled(true);
                    whiteboardPanel.repaint();
                }
            }
        });

        // Group the toggle buttons
        ButtonGroup group = new ButtonGroup();
        group.add(selectButton);
        group.add(strokeColorButton);
        group.add(fillColorButton);

        // Stroke size chooser
        JLabel strokeLabel = new JLabel("선 굵기:");
        strokeLabel.setFont(strokeLabel.getFont().deriveFont(Font.BOLD));
        JSlider strokeSlider = new JSlider(1, 10, 1);
        strokeSlider.addChangeListener(e -> whiteboardPanel.setCurrentStroke(strokeSlider.getValue()));

        // Layout setup using GridBagLayout
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(selectButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        add(strokeColorButton, gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        add(fillColorButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3; // Span across 3 columns
        add(strokeLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3; // Span across 3 columns
        add(strokeSlider, gbc);
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
