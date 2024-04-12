package org.example.client;

import org.example.service.socket.JsonSocketServiceImpl;

import java.net.Socket;

public class JsonClient {
    private String host;
    private int port;

    public JsonClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void startClient() {
        try (Socket socket = new Socket(host, port)) {
            JsonSocketServiceImpl service = new JsonSocketServiceImpl(socket);
            service.start();

            // 서버로 메시지를 보내고 응답을 받습니다.
            // 예: service.sendMessage(new Message("Hello, server!"));
            // Message response = service.receiveMessage(Message.class);

            service.stop();
        } catch (Exception e) {
            System.out.println("Client exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

