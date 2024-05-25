package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private static String SERVER_ADDRESS;
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java Client <server address>");
            return;
        }

        SERVER_ADDRESS = args[0];

        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            System.out.println("서버에 접속되었습니다.");
            String serverMessage;
            while ((serverMessage = in.readLine()) != null) {
                System.out.println("서버: " + serverMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
