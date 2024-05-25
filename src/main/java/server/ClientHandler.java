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
                    Server.sendCurrentState(this); // 새로운 클라이언트에 현재 상태 전송
                } else if (message.startsWith("DISCONNECT:")) {
                    String disconnectingClientId = message.substring(11);
                    System.out.println("클라이언트 ID: " + disconnectingClientId + "님이 접속 해제했습니다.");
                    Server.broadcast("LEAVE:" + disconnectingClientId);
                    Server.removeClient(this);
                    break;
                } else if (message.startsWith("SHAPE:")) {
                    System.out.println("도형 정보 수신: " + message);
                    Server.addShape(message.substring(6)); // 도형 정보를 리스트에 추가하고 브로드캐스트
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
