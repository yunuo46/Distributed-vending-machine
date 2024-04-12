package org.example;

import org.example.service.socket.JsonSocketService;
import org.example.service.socket.JsonSocketServiceImpl;

import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        try {
            // 서버 소켓 생성 및 포트 지정
            ServerSocket serverSocket = new ServerSocket(8888);
            System.out.println("서버 시작: 포트 8888 대기 중...");

            // 클라이언트의 연결을 대기하고 소켓을 생성
            Socket clientSocket = serverSocket.accept();
            System.out.println("클라이언트 연결됨");

            // 소켓을 이용하여 JsonSocketService 구현체 생성
            JsonSocketService jsonSocketService = new JsonSocketServiceImpl(clientSocket);
            jsonSocketService.start();

            // 클라이언트로부터 메시지 수신 및 출력
            String receivedMessage = jsonSocketService.receiveMessage(String.class);
            System.out.println("클라이언트로부터 수신된 메시지: " + receivedMessage);

            // 서버 종료
            jsonSocketService.stop();
            serverSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}