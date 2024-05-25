package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private static final int PORT = 12345;
    private static List<ClientHandler> clients = new ArrayList<>();
    private static List<String> shapes = new ArrayList<>(); // 현재 상태를 저장하는 리스트

    public static void main(String[] args) {
        System.out.println("서버가 시작되었습니다...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("클라이언트가 접속했습니다: " + clientSocket.getInetAddress());
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
                sendClientList();
                sendCurrentState(clientHandler); // 새로운 클라이언트에 현재 상태 전송
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
        sendClientList();
    }

    public static void sendCurrentState(ClientHandler clientHandler) {
        for (String shape : shapes) {
            clientHandler.sendMessage("SHAPE:" + shape);
        }
    }

    public static void addShape(String shape) {
        shapes.add(shape);
        broadcast("SHAPE:" + shape);
    }
}
