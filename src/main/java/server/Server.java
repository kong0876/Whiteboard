package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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

class ClientHandler implements Runnable {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String clientId;

    public ClientHandler(Socket socket) {
        this.socket = socket;
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
                } else if (message.startsWith("DISCONNECT:")) {
                    String disconnectingClientId = message.substring(11);
                    System.out.println("클라이언트 ID: " + disconnectingClientId + "님이 접속 해제했습니다.");
                    Server.broadcast("LEAVE:" + disconnectingClientId);
                    Server.removeClient(this);
                    break;
                } else {
                    // 다른 메시지 처리
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

    public String getClientId() {
        return clientId;
    }

    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }
}
