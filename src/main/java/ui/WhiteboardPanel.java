package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import client.WhiteboardClient;

public class WhiteboardPanel extends JPanel {
    private List<Shape> shapes;
    private Shape currentShape;
    private String currentAction;
    private Color currentStrokeColor;
    private Color currentFillColor;
    private int currentStroke;
    private boolean fillShape;
    private Shape selectedShape;
    private Point prevMousePoint;
    private WhiteboardClient client;

    public WhiteboardPanel(WhiteboardClient client) {
        this.client = client;
        setBackground(Color.WHITE);
        shapes = new ArrayList<>();
        currentStrokeColor = Color.BLACK;
        currentFillColor = Color.BLACK;
        currentStroke = 1;
        fillShape = false;
        currentAction = "선택"; // 초기 값 설정

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if ("선택".equals(currentAction)) {
                    selectShape(e.getX(), e.getY());
                    prevMousePoint = e.getPoint();
                } else {
                    createShape(e.getX(), e.getY());
                }
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (currentShape != null) {
                    // 도형의 최종 상태를 서버로 전송
                    client.sendMessage("SHAPE:" + currentShape.serialize());
                    currentShape = null;
                }
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if ("선택".equals(currentAction) && selectedShape != null) {
                    Point currentPoint = e.getPoint();
                    int dx = currentPoint.x - prevMousePoint.x;
                    int dy = currentPoint.y - prevMousePoint.y;
                    selectedShape.move(dx, dy);
                    prevMousePoint = currentPoint;
                } else if (currentShape != null) {
                    currentShape.update(e.getX(), e.getY());
                }
                repaint();
            }
        });
    }

    private void createShape(int x, int y) {
        switch (currentAction) {
            case "원":
                currentShape = new Shape(x, y, currentStrokeColor, currentFillColor, currentStroke, fillShape, Shape.ShapeType.CIRCLE);
                break;
            case "사각형":
                currentShape = new Shape(x, y, currentStrokeColor, currentFillColor, currentStroke, fillShape, Shape.ShapeType.RECTANGLE);
                break;
            case "선":
                currentShape = new Shape(x, y, currentStrokeColor, currentFillColor, currentStroke, false, Shape.ShapeType.LINE);
                break;
            case "텍스트":
                String text = JOptionPane.showInputDialog("텍스트 입력:");
                if (text != null && !text.isEmpty()) {
                    currentShape = new Shape(x, y, currentStrokeColor, currentFillColor, currentStroke, false, Shape.ShapeType.TEXT);
                    currentShape.setText(text);
                }
                break;
            default:
                return; // currentAction이 유효하지 않을 경우 메서드 종료
        }
        if (currentShape != null) {
            synchronized (shapes) {
                shapes.add(currentShape);
            }
            selectedShape = currentShape;
        }
    }

    private void selectShape(int x, int y) {
        selectedShape = null;
        synchronized (shapes) {
            for (Shape shape : shapes) {
                if (shape.contains(x, y)) {
                    selectedShape = shape;
                    currentShape = shape;
                    currentStrokeColor = shape.getStrokeColor();
                    currentFillColor = shape.getFillColor();
                    currentStroke = shape.getStroke();
                    fillShape = shape.isFilled();
                    break;
                }
            }
        }
    }

    public Shape getSelectedShape() {
        return selectedShape;
    }

    public void setCurrentAction(String action) {
        this.currentAction = action;
    }

    public void setCurrentStrokeColor(Color color) {
        this.currentStrokeColor = color;
        if (selectedShape != null) {
            selectedShape.setStrokeColor(color);
            repaint();
        }
    }

    public void setCurrentFillColor(Color color) {
        this.currentFillColor = color;
        if (selectedShape != null) {
            selectedShape.setFillColor(color);
            repaint();
        }
    }

    public void setCurrentStroke(int stroke) {
        this.currentStroke = stroke;
        if (selectedShape != null) {
            selectedShape.setStroke(stroke);
            repaint();
        }
    }

    public void setFillShape(boolean fill) {
        this.fillShape = fill;
        if (selectedShape != null) {
            selectedShape.setFilled(fill);
            repaint();
        }
    }

    public void updateShape(Shape shape) {
        synchronized (shapes) {
            shapes.add(shape);
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        synchronized (shapes) {
            for (Shape shape : shapes) {
                shape.draw(g);
            }
        }
        if (selectedShape != null) {
            selectedShape.drawSelection(g);
        }
    }
}
