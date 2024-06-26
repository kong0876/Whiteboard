package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import client.WhiteboardClient;

public class WhiteboardPanel extends JPanel {
    private final List<Shape> shapes;
    private Shape currentShape;
    private String currentAction;
    private Color currentStrokeColor;
    private Color currentFillColor;
    private int currentStroke;
    private boolean fillShape;
    private Shape selectedShape;
    private Point prevMousePoint;
    private final WhiteboardClient client;
    private final Map<String, String> shapeLocks;

    public WhiteboardPanel(WhiteboardClient client) {
        this.client = client;
        setBackground(Color.WHITE);
        shapes = new ArrayList<>();
        shapeLocks = new HashMap<>();
        currentStrokeColor = Color.BLACK;
        currentFillColor = Color.BLACK;
        currentStroke = 1;
        fillShape = false;
        currentAction = "선택";

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
                    client.sendMessage("SHAPE:" + currentShape.serialize());
                    currentShape = null;
                } else if (selectedShape != null) {
                    client.sendMessage("SHAPE:" + selectedShape.serialize());
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
                    client.sendMessage("SHAPE:" + selectedShape.serialize());
                    repaint();
                } else if (currentShape != null) {
                    currentShape.update(e.getX(), e.getY());
                    client.sendMessage("SHAPE:" + currentShape.serialize());
                    repaint();
                }
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
                    synchronized (shapes) {
                        shapes.add(currentShape);
                    }
                    client.sendMessage("SHAPE:" + currentShape.serialize());
                    currentShape = null; // 텍스트 입력 후 즉시 전송하고 null로 설정
                }
                break;
            default:
                return;
        }
        if (currentShape != null) {
            synchronized (shapes) {
                shapes.add(currentShape);
            }
            if (selectedShape != null) {
                client.sendMessage("UNLOCK:" + selectedShape.getId() + ":" + client.getClientId());
            }
            selectedShape = currentShape;
            client.sendMessage("LOCK:" + currentShape.getId() + ":" + client.getClientId());
        }
    }

    private void selectShape(int x, int y) {
        Shape shapeToSelect = null;
        synchronized (shapes) {
            for (Shape shape : shapes) {
                if (shape.contains(x, y)) {
                    shapeToSelect = shape;
                    break;
                }
            }
        }

        if (shapeToSelect != null) {
            if (shapeLocks.containsKey(shapeToSelect.getId())) {
                if (!shapeLocks.get(shapeToSelect.getId()).equals(client.getClientId())) {
                    JOptionPane.showMessageDialog(this, "도형을 수정할 수 없습니다.");
                    return;
                }
            } else {
                if (selectedShape != null) {
                    client.sendMessage("UNLOCK:" + selectedShape.getId() + ":" + client.getClientId());
                }
                client.sendMessage("LOCK:" + shapeToSelect.getId() + ":" + client.getClientId());
            }
            selectedShape = shapeToSelect;
            currentShape = shapeToSelect;
            currentStrokeColor = shapeToSelect.getStrokeColor();
            currentFillColor = shapeToSelect.getFillColor();
            currentStroke = shapeToSelect.getStroke();
            fillShape = shapeToSelect.isFilled();
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
            client.sendMessage("SHAPE:" + selectedShape.serialize());
            repaint();
        }
    }

    public void setCurrentFillColor(Color color) {
        this.currentFillColor = color;
        if (selectedShape != null) {
            selectedShape.setFillColor(color);
            client.sendMessage("SHAPE:" + selectedShape.serialize());
            repaint();
        }
    }

    public void setCurrentStroke(int stroke) {
        this.currentStroke = stroke;
        if (selectedShape != null) {
            selectedShape.setStroke(stroke);
            client.sendMessage("SHAPE:" + selectedShape.serialize());
            repaint();
        }
    }

    public void setFillShape(boolean fill) {
        this.fillShape = fill;
        if (selectedShape != null) {
            selectedShape.setFilled(fill);
            client.sendMessage("SHAPE:" + selectedShape.serialize());
            repaint();
        }
    }

    public void updateShape(Shape shape) {
        synchronized (shapes) {
            shapes.removeIf(s -> s.getId().equals(shape.getId()));
            shapes.add(shape);
        }
        repaint();
    }

    public void lockShape(String shapeId, String ownerId) {
        synchronized (shapeLocks) {
            shapeLocks.put(shapeId, ownerId);
        }
        repaint();
    }

    public void unlockShape(String shapeId, String ownerId) {
        synchronized (shapeLocks) {
            if (shapeLocks.get(shapeId).equals(ownerId)) {
                shapeLocks.remove(shapeId);
            }
        }
        repaint();
    }

    public void lockFailed(String shapeId) {
        JOptionPane.showMessageDialog(this, "도형을 잠그는 데 실패했습니다: " + shapeId);
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
            selectedShape.drawSelection(g, Color.BLUE);
        }
        synchronized (shapeLocks) {
            for (Map.Entry<String, String> entry : shapeLocks.entrySet()) {
                String shapeId = entry.getKey();
                String ownerId = entry.getValue();
                synchronized (shapes) {
                    for (Shape shape : shapes) {
                        if (shape.getId().equals(shapeId) && shape != selectedShape) {
                            shape.drawSelection(g, ownerId.equals(client.getClientId()) ? Color.BLUE : Color.RED);
                        }
                    }
                }
            }
        }
    }

    public void saveShapes(String filename) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            synchronized (shapes) {
                out.writeObject(shapes);
            }
            System.out.println("그림이 저장되었습니다: " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadShapes(String filename) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            List<Shape> loadedShapes = (List<Shape>) in.readObject();
            synchronized (shapes) {
                shapes.clear();
            }
            client.sendMessage("CLEAR");

            synchronized (shapes) {
                shapes.addAll(loadedShapes);
            }
            selectedShape = null;
            repaint();
            System.out.println("그림이 로드되었습니다: " + filename);

            for (Shape shape : loadedShapes) {
                client.sendMessage("SHAPE:" + shape.serialize());
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void clearShapes() {
        synchronized (shapes) {
            shapes.clear();
        }
        repaint();
    }
}
