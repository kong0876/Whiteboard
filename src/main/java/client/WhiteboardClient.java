package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import ui.WhiteboardFrame;
import ui.Shape;

public class WhiteboardClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;
    private String clientId;
    private WhiteboardFrame frame;

    public WhiteboardClient(String clientId) {
        this.clientId = clientId;
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
            String shapeData = message.substring(6);
            System.out.println("도형 정보 수신: " + shapeData); // 추가된 로그
            Shape shape = Shape.deserialize(shapeData);
            if (frame != null) {
                frame.getWhiteboardPanel().updateShape(shape);
            }
        }
    }


    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }
}
