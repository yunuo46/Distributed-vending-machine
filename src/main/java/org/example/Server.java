package org.example;

import org.example.model.Stock;
import org.example.service.socket.JsonSocketService;
import org.example.service.socket.JsonSocketServiceImpl;

import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;

public class Server {
    public static void main(String[] args) {
        String jdbcUrl = System.getenv("DB_URL");
        String username = System.getenv("DB_USERNAME");
        String password = System.getenv("DB_PASSWORD");
        try {
            // rds 연결
            Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
            System.out.println("Database connected!");

            // Stock 클래스에 Connection 전달
            Stock stock = new Stock(connection);
            stock.editStock(1,3);
            stock.checkStock(1);
            stock.saleStock(1,2);
            stock.checkStock(1);

            // 서버 소켓 생성 및 포트 지정
            ServerSocket serverSocket = new ServerSocket(8888);
            System.out.println("waiting for connection on port 8888...");

            // 클라이언트의 연결을 대기하고 소켓을 생성
            Socket clientSocket = serverSocket.accept();
            System.out.println("client connected!");

            // 소켓을 이용하여 JsonSocketService 구현체 생성
            JsonSocketService jsonSocketService = new JsonSocketServiceImpl(clientSocket);
            jsonSocketService.start();

            // 클라이언트로부터 메시지 수신 및 출력
            String receivedMessage = jsonSocketService.receiveMessage(String.class);
            System.out.println("received message: " + receivedMessage);

            // 서버 종료
            jsonSocketService.stop();
            serverSocket.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}