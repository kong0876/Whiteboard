package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import ui.WhiteboardFrame;
import ui.Shape;
import javax.swing.JOptionPane;

public class WhiteboardClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private final String SERVER_ADDRESS;
    private static final int SERVER_PORT = 12345;
    private String clientId;
    private WhiteboardFrame frame;

    public WhiteboardClient(String clientId, String serverAddress) {
        this.clientId = clientId;
        this.SERVER_ADDRESS = serverAddress;
    }

    public void setFrame(WhiteboardFrame frame) {
        this.frame = frame;
    }

    public void connect() {
        try {
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println("ID:" + clientId);
            System.out.println("서버에 접속되었습니다.");
            new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = in.readLine()) != null) {
                        System.out.println("서버: " + serverMessage);
                        handleMessage(serverMessage);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "서버에 연결할 수 없습니다. 프로그램을 종료합니다.", "연결 오류", JOptionPane.ERROR_MESSAGE);
            System.exit(1); // 프로그램 종료
        }

        // 윈도우 종료 시 리스너 추가
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            sendMessage("DISCONNECT:" + clientId);
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
    }

    private void handleMessage(String message) {
        if (message.startsWith("CLIENTS:")) {
            String[] clients = message.substring(8).split(",");
            if (frame != null) {
                frame.updateClientList(clients);
            }
        } else if (message.startsWith("JOIN:")) {
            String newClient = message.substring(5);
            if (frame != null) {
                frame.addClient(newClient);
            }
        } else if (message.startsWith("LEAVE:")) {
            String leavingClient = message.substring(6);
            if (frame != null) {
                frame.removeClient(leavingClient);
            }
        } else if (message.startsWith("SHAPE:")) {
            Shape shape = Shape.deserialize(message.substring(6));
            if (frame != null) {
                frame.getWhiteboardPanel().updateShape(shape);
            }
        } else if (message.startsWith("CLEAR")) {
            if (frame != null) {
                frame.getWhiteboardPanel().clearShapes();
            }
        } else if (message.startsWith("LOCK:")) {
            String[] parts = message.substring(5).split(":");
            String shapeId = parts[0];
            String ownerId = parts[1];
            if (frame != null) {
                frame.getWhiteboardPanel().lockShape(shapeId, ownerId);
            }
        } else if (message.startsWith("UNLOCK:")) {
            String[] parts = message.substring(7).split(":");
            String shapeId = parts[0];
            String ownerId = parts[1];
            if (frame != null) {
                frame.getWhiteboardPanel().unlockShape(shapeId, ownerId);
            }
        }
    }

    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }

    public String getClientId() {
        return clientId;
    }
}
