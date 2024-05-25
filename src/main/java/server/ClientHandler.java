package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import ui.Shape;

public class ClientHandler implements Runnable {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String clientId;
    private ConcurrentMap<String, String> shapeLocks;
    private List<Shape> shapes;

    public ClientHandler(Socket socket, ConcurrentMap<String, String> shapeLocks, List<Shape> shapes) {
        this.socket = socket;
        this.shapeLocks = shapeLocks;
        this.shapes = shapes;
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String message;
            while ((message = in.readLine()) != null) {
                if (message.startsWith("ID:")) {
                    clientId = message.substring(3);
                    System.out.println("클라이언트 ID: " + clientId);
                    Server.broadcast("JOIN:" + clientId);
                    Server.sendClientList();
                    sendExistingShapesAndLocks(); // 새 클라이언트에게 기존 도형 정보와 잠금 상태를 전송
                } else if (message.startsWith("DISCONNECT:")) {
                    String disconnectingClientId = message.substring(11);
                    System.out.println("클라이언트 ID: " + disconnectingClientId + "님이 접속 해제했습니다.");
                    Server.broadcast("LEAVE:" + disconnectingClientId);
                    Server.removeClient(this);
                    break;
                } else if (message.startsWith("SHAPE:")) {
                    Shape shape = Shape.deserialize(message.substring(6));
                    Server.updateShape(shape); // 서버에 도형 정보 업데이트
                    Server.broadcast(message);
                } else if (message.startsWith("CLEAR")) {
                    // 모든 클라이언트에게 CLEAR 명령 전송
                    Server.broadcast("CLEAR");
                } else if (message.startsWith("LOCK:")) {
                    String[] parts = message.substring(5).split(":");
                    if (parts.length == 2) { // 메시지 형식이 올바른지 확인
                        String shapeId = parts[0];
                        String ownerId = parts[1];
                        shapeLocks.put(shapeId, ownerId);
                        Server.broadcast(message);
                    } else {
                        System.out.println("잘못된 LOCK 메시지 형식: " + message);
                    }
                } else if (message.startsWith("UNLOCK:")) {
                    String[] parts = message.substring(7).split(":");
                    if (parts.length == 2) { // 메시지 형식이 올바른지 확인
                        String shapeId = parts[0];
                        String ownerId = parts[1];
                        shapeLocks.remove(shapeId, ownerId);
                        Server.broadcast(message);
                    } else {
                        System.out.println("잘못된 UNLOCK 메시지 형식: " + message);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendExistingShapesAndLocks() {
        synchronized (shapes) {
            for (Shape shape : shapes) {
                sendMessage("SHAPE:" + shape.serialize());
            }
        }
        synchronized (shapeLocks) {
            for (String shapeId : shapeLocks.keySet()) {
                sendMessage("LOCK:" + shapeId + ":" + shapeLocks.get(shapeId));
            }
        }
    }

    public String getClientId() {
        return clientId;
    }

    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }
}
