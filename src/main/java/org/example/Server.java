package org.example;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import org.example.controller.Machine;
import org.example.service.socket.JsonSocketService;
import org.example.service.socket.JsonSocketServiceImpl;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
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

            // 소켓 서버 스레드 시작
            new Thread(() -> startSocketServer(connection)).start();

            // HTTP 서버 스레드 시작
            new Thread(() -> startHttpServer(connection)).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void startSocketServer(Connection connection) {
        try {
            // 서버 소켓 생성 및 포트 지정
            ServerSocket serverSocket = new ServerSocket(8888);
            System.out.println("Waiting for connection on port 8888...");

            // 클라이언트의 연결을 대기하고 소켓을 생성
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected!");

            // 소켓을 이용하여 JsonSocketService 구현체 생성
            JsonSocketService jsonSocketService = new JsonSocketServiceImpl(clientSocket);
            jsonSocketService.start();

            // 머신 생성
            Machine machine = new Machine(jsonSocketService, connection);

            // 클라이언트로부터 메시지 수신 및 출력
            while (true) {
                try {
                    JsonObject receivedMessage = jsonSocketService.receiveMessage(JsonObject.class);
                    String msgType = receivedMessage.get("msg_type").getAsString();
                    if (msgType.equals("req_stock")) {
                        machine.stockResponse(receivedMessage);
                    } else if (msgType.equals("req_prepayment")) {
                        machine.prepaymentResponse(receivedMessage);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }

            // 서버 종료
            jsonSocketService.stop();
            serverSocket.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void startHttpServer(Connection connection) {
        try {
            // HTTP 서버 생성 및 포트 지정
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
            Machine machine = new Machine(null, connection);

            server.createContext("/api/select", new SelectItemHandler(machine));
            server.setExecutor(null);
            server.start();
            System.out.println("HTTP Server started on port 8080");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class SelectItemHandler implements HttpHandler {
        private final Machine machine;

        public SelectItemHandler(Machine machine) {
            this.machine = machine;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                addCorsHeaders(exchange);
                JsonObject message = parseRequest(exchange);
                int item_code = message.get("item_code").getAsInt();
                int item_num = message.get("item_num").getAsInt();
                machine.selectItem(item_code, item_num);

                JsonObject jsonResponse = new JsonObject();
                jsonResponse.addProperty("stock", false);
                jsonResponse.addProperty("prepayment", false);
                sendJsonResponse(exchange, jsonResponse);
            } else if ("OPTIONS".equals(exchange.getRequestMethod())) {
                addCorsHeaders(exchange);
                exchange.sendResponseHeaders(204, -1); // No Content
            } else {
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            }
        }

        private JsonObject parseRequest(HttpExchange exchange) throws IOException {
            InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
            JsonObject message = JsonParser.parseReader(isr).getAsJsonObject();
            isr.close();
            return message;
        }

        private void sendJsonResponse(HttpExchange exchange, JsonObject jsonResponse) throws IOException {
            String response = jsonResponse.toString();
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            addCorsHeaders(exchange);
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

        private void addCorsHeaders(HttpExchange exchange) {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
        }
    }
}
