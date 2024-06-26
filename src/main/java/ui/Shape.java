package ui;

import java.awt.*;
import java.io.Serializable;
import java.util.UUID;

public class Shape implements Serializable {
    public enum ShapeType {CIRCLE, RECTANGLE, LINE, TEXT}

    private static final long serialVersionUID = 1L;
    private String id; // 고유 식별자
    private int x, y, width, height;
    private Color strokeColor;
    private Color fillColor;
    private int stroke;
    private boolean fill;
    private ShapeType shapeType;
    private String text;
    private transient FontMetrics fontMetrics;

    public Shape(int x, int y, Color strokeColor, Color fillColor, int stroke, boolean fill, ShapeType shapeType) {
        this.id = UUID.randomUUID().toString(); // 고유 식별자 생성
        this.x = x;
        this.y = y;
        this.strokeColor = strokeColor;
        this.fillColor = fillColor;
        this.stroke = stroke;
        this.fill = fill;
        this.shapeType = shapeType;
    }

    public void draw(Graphics g) {
        g.setColor(strokeColor);
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(stroke));
        switch (shapeType) {
            case CIRCLE -> {
                if (fill) {
                    g.setColor(fillColor);
                    g.fillOval(x - width / 2, y - height / 2, width, height);
                    g.setColor(strokeColor);
                    g.drawOval(x - width / 2, y - height / 2, width, height);
                } else {
                    g.drawOval(x - width / 2, y - height / 2, width, height);
                }
            }
            case RECTANGLE -> {
                if (fill) {
                    g.setColor(fillColor);
                    g.fillRect(x, y, width, height);
                    g.setColor(strokeColor);
                    g.drawRect(x, y, width, height);
                } else {
                    g.drawRect(x, y, width, height);
                }
            }
            case LINE -> g.drawLine(x, y, x + width, y + height);
            case TEXT -> {
                g.setFont(new Font("Arial", Font.PLAIN, stroke * 10));
                g.drawString(text, x, y);
                if (fontMetrics == null) {
                    fontMetrics = g.getFontMetrics();
                }
            }
        }
    }

    public void drawSelection(Graphics g, Color color) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(1));
        g2.setColor(color);
        switch (shapeType) {
            case CIRCLE -> g2.drawOval(x - width / 2 - 3, y - height / 2 - 3, width + 6, height + 6);
            case RECTANGLE -> g2.drawRect(x - 3, y - 3, width + 6, height + 6);
            case LINE -> {
                g2.drawLine(x - 3, y - 3, x + width + 3, y + height + 3);
                g2.drawLine(x + 3, y + 3, x + width - 3, y + height - 3);
            }
            case TEXT -> {
                if (fontMetrics == null) {
                    fontMetrics = g.getFontMetrics(new Font("Arial", Font.PLAIN, stroke * 10));
                }
                int textWidth = fontMetrics.stringWidth(text);
                int textHeight = fontMetrics.getHeight();
                g2.drawRect(x - 3, y - textHeight - 3, textWidth + 6, textHeight + 6);
            }
        }
    }

    public void update(int x, int y) {
        switch (shapeType) {
            case CIRCLE -> {
                this.width = Math.abs(x - this.x) * 2;
                this.height = this.width;
            }
            case RECTANGLE -> {
                this.width = Math.abs(x - this.x);
                this.height = Math.abs(y - this.y);
            }
            case LINE -> {
                this.width = x - this.x;
                this.height = y - this.y;
            }
            case TEXT -> {
                // Text doesn't need to update coordinates on drag
            }
        }
    }

    public boolean contains(int x, int y) {
        switch (shapeType) {
            case CIRCLE -> {
                int radius = width / 2;
                return Math.pow(x - this.x, 2) + Math.pow(y - this.y, 2) <= Math.pow(radius, 2);
            }
            case RECTANGLE -> {
                return x >= this.x && x <= this.x + width && y >= this.y && y <= this.y + height;
            }
            case LINE -> {
                double distance = Math.abs((height) * x - (width) * y + width * this.y - height * this.x) /
                        Math.sqrt(Math.pow(height, 2) + Math.pow(width, 2));
                return distance <= stroke / 2;
            }
            case TEXT -> {
                if (fontMetrics == null) {
                    fontMetrics = new FontMetrics(new Font("Arial", Font.PLAIN, stroke * 10)) {};
                }
                int textWidth = fontMetrics.stringWidth(text);
                int textHeight = fontMetrics.getHeight();
                return x >= this.x && x <= this.x + textWidth && y >= this.y - textHeight && y <= this.y;
            }
        }
        return false;
    }

    public void move(int dx, int dy) {
        x += dx;
        y += dy;
    }

    public void setStrokeColor(Color color) {
        this.strokeColor = color;
    }

    public Color getStrokeColor() {
        return strokeColor;
    }

    public void setFillColor(Color color) {
        this.fillColor = color;
    }

    public Color getFillColor() {
        return fillColor;
    }

    public void setStroke(int stroke) {
        this.stroke = stroke;
    }

    public int getStroke() {
        return stroke;
    }

    public void setFilled(boolean fill) {
        this.fill = fill;
    }

    public boolean isFilled() {
        return fill;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public String serialize() {
        return id + "," + shapeType + "," + x + "," + y + "," + width + "," + height + "," + strokeColor.getRGB() + "," +
                fillColor.getRGB() + "," + stroke + "," + fill + (shapeType == ShapeType.TEXT ? "," + text : "");
    }

    public static Shape deserialize(String data) {
        String[] parts = data.split(",");
        String id = parts[0];
        ShapeType type = ShapeType.valueOf(parts[1]);
        int x = Integer.parseInt(parts[2]);
        int y = Integer.parseInt(parts[3]);
        int width = Integer.parseInt(parts[4]);
        int height = Integer.parseInt(parts[5]);
        Color strokeColor = new Color(Integer.parseInt(parts[6]));
        Color fillColor = new Color(Integer.parseInt(parts[7]));
        int stroke = Integer.parseInt(parts[8]);
        boolean fill = Boolean.parseBoolean(parts[9]);

        Shape shape = new Shape(x, y, strokeColor, fillColor, stroke, fill, type);
        shape.id = id;
        shape.width = width;
        shape.height = height;
        if (type == ShapeType.TEXT) {
            shape.setText(parts[10]);
        }
        return shape;
    }
}
