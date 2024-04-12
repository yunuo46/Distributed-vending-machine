package org.example.server;

import java.net.ServerSocket;
import java.net.Socket;
import org.example.service.socket.JsonSocketServiceImpl;

public class JsonServer {
    private int port;

    public JsonServer(int port) {
        this.port = port;
    }

    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                JsonSocketServiceImpl service = new JsonSocketServiceImpl(clientSocket);
                service.start();

                // 여기서 클라이언트로부터 메시지를 받고, 응답을 보낼 수 있습니다.
                // 예: service.sendMessage("Hello from server");

                service.stop();
            }
        } catch (Exception e) {
            System.out.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
