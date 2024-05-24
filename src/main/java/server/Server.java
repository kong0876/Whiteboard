package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private static final int PORT = 12345;
    private static List<ClientHandler> clients = new ArrayList<>();

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
}
