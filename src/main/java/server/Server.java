package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import ui.Shape;

public class Server {
    private static final int PORT = 12345;
    private static List<ClientHandler> clients = new ArrayList<>();
    private static ConcurrentMap<String, String> shapeLocks = new ConcurrentHashMap<>();
    private static List<Shape> shapes = new ArrayList<>(); // 도형 정보를 저장하는 리스트

    public static void main(String[] args) {
        System.out.println("서버가 시작되었습니다...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("클라이언트가 접속했습니다: " + clientSocket.getInetAddress());
                ClientHandler clientHandler = new ClientHandler(clientSocket, shapeLocks, shapes);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
                sendClientList();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void broadcast(String message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    public static void sendClientList() {
        StringBuilder clientList = new StringBuilder("CLIENTS:");
        for (ClientHandler client : clients) {
            clientList.append(client.getClientId()).append(",");
        }
        broadcast(clientList.toString());
    }

    public static void removeClient(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        unlockShapesByClient(clientHandler.getClientId());
        sendClientList();
    }

    private static void unlockShapesByClient(String clientId) {
        for (String shapeId : shapeLocks.keySet()) {
            if (shapeLocks.get(shapeId).equals(clientId)) {
                shapeLocks.remove(shapeId);
                broadcast("UNLOCK:" + shapeId + ":" + clientId);
            }
        }
    }

    public static void addShape(Shape shape) {
        synchronized (shapes) {
            shapes.add(shape);
        }
    }

    public static void updateShape(Shape shape) {
        synchronized (shapes) {
            shapes.removeIf(s -> s.getId().equals(shape.getId()));
            shapes.add(shape);
        }
    }

    public static List<Shape> getShapes() {
        return shapes;
    }

    public static ConcurrentMap<String, String> getShapeLocks() {
        return shapeLocks;
    }
}
