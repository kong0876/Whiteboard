package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
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
                } else if (message.startsWith("SHAPE:")) {
                    System.out.println("도형 정보 수신: " + message); // 추가된 로그
                    Server.broadcast(message);
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
