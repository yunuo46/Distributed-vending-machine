package org.example;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import org.example.controller.Machine;
import org.example.service.socket.JsonSocketService;
import org.example.service.socket.JsonSocketServiceImpl;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
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
        Gson gson = new Gson();
        try (ServerSocket serverSocket = new ServerSocket(8888)) {
            System.out.println("Waiting for connection on port 8888...");

            while (true) {
                try (Socket clientSocket = serverSocket.accept()) {
                    System.out.println("Client connected!");

                    JsonSocketService jsonSocketService = new JsonSocketServiceImpl(clientSocket);
                    jsonSocketService.start();
                    Machine machine = new Machine(jsonSocketService, connection, null);

                    while (true) {
                        try {
                            JsonObject receivedMessage = gson.fromJson(jsonSocketService.receiveMessage(),JsonObject.class);
                            if (receivedMessage == null) break;
                            String msgType = receivedMessage.get("msg_type").getAsString();
                            if (msgType.equals("req_stock")) {
                                System.out.println("Received stock request!");
                                machine.stockResponse(receivedMessage);
                            } else if (msgType.equals("req_prepay")) {
                                System.out.println("Received prepay request!");
                                machine.prepaymentResponse(receivedMessage);
                            } else {
                                break;
                            }
                        } catch (Exception e) {
                            break;
                        }
                    }
                    System.out.println("Client disconnected!");
                } catch (SocketException se) {
                    System.out.println("Server socket error: " + se.getMessage());
                } catch (IOException ioe) {
                    System.out.println("I/O error while waiting for client: " + ioe.getMessage());
                } catch (Exception e) {
                    System.out.println("Unexpected error while waiting for client: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println("Server error: " + e.getMessage());
        } finally {
            try {
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void startHttpServer(Connection connection) {
        try {
            // HTTP 서버 생성 및 포트 지정
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

            server.createContext("/api/select", new SelectItemHandler(connection));
            server.createContext("/api/payment", new InsertCardHandler(connection));
            server.createContext("/api/payment/pre", new InsertCardPreHandler(connection));
            server.createContext("/api/prepayment", new ProcessPrePaymentHandler(connection));
            server.createContext("/api/refund", new RefundPrepaymentHandler(connection));
            server.createContext("/api/code", new InsertCodeHandler(connection));
            server.createContext("/api/admin/stock", new ManageStockHandler(connection));
            server.createContext("/api/admin/add-dvm", new AddDVMHandler(connection));
            server.createContext("/api/admin/remove-dvm", new RemoveDVMHandler(connection));

            server.setExecutor(null);
            server.start();
            System.out.println("HTTP Server started on port 8080");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class SelectItemHandler implements HttpHandler {
        private final Connection connection;

        public SelectItemHandler(Connection connection) {
            this.connection = connection;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("POST".equals(exchange.getRequestMethod())) {
                JsonObject message = parseRequest(exchange);
                String item_code = message.get("item_code").getAsString();
                int item_num = message.get("item_num").getAsInt();

                // Machine 생성
                Machine machine = new Machine(null, connection, exchange);
                System.out.println("select item api");
                machine.selectItem(item_code, item_num);
            } else if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1); // No Content
            } else {
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            }
        }
    }

    static class InsertCardHandler implements HttpHandler {
        private final Connection connection;

        public InsertCardHandler(Connection connection) {
            this.connection = connection;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("POST".equals(exchange.getRequestMethod())) {
                JsonObject message = parseRequest(exchange);
                String card_data = message.get("card_data").getAsString();
                String item_code = message.get("item_code").getAsString();
                int item_num = message.get("item_num").getAsInt();

                // Machine 생성
                Machine machine = new Machine(null, connection, exchange);
                System.out.println("insert card api");
                machine.insertCardData(card_data, item_code, item_num, false);
            } else if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1); // No Content
            } else {
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            }
        }
    }

    static class InsertCardPreHandler implements HttpHandler {
        private final Connection connection;

        public InsertCardPreHandler(Connection connection) {
            this.connection = connection;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("POST".equals(exchange.getRequestMethod())) {
                JsonObject message = parseRequest(exchange);
                String card_data = message.get("card_data").getAsString();
                String item_code = message.get("item_code").getAsString();
                int item_num = message.get("item_num").getAsInt();

                // Machine 생성
                Machine machine = new Machine(null, connection, exchange);
                System.out.println("insert card to prepayment api");
                machine.insertCardData(card_data, item_code, item_num, true);
            } else if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1); // No Content
            } else {
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            }
        }
    }

    static class ProcessPrePaymentHandler implements HttpHandler {
        private final Connection connection;

        public ProcessPrePaymentHandler(Connection connection) {
            this.connection = connection;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("POST".equals(exchange.getRequestMethod())) {
                JsonObject message = parseRequest(exchange);
                String dvm_id = message.get("dvm_id").getAsString();
                String item_code = message.get("item_code").getAsString();
                int item_num = message.get("item_num").getAsInt();

                // Machine 생성
                Machine machine = new Machine(null, connection, exchange);
                System.out.println("process prepayment api");
                machine.ProcessPrepayment(dvm_id, item_code, item_num);
            } else if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1); // No Content
            } else {
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            }
        }
    }

    static class RefundPrepaymentHandler implements HttpHandler {
        private final Connection connection;

        public RefundPrepaymentHandler(Connection connection) {
            this.connection = connection;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("POST".equals(exchange.getRequestMethod())) {
                JsonObject message = parseRequest(exchange);
                String card_data = message.get("card_data").getAsString();
                String item_code = message.get("item_code").getAsString();
                int item_num = message.get("item_num").getAsInt();

                // Machine 생성
                Machine machine = new Machine(null, connection, exchange);
                System.out.println("refund prepayment api");
                machine.refundPrepayment(card_data, item_code, item_num);
            } else if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1); // No Content
            } else {
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            }
        }
    }


    static class InsertCodeHandler implements HttpHandler {
        private final Connection connection;

        public InsertCodeHandler(Connection connection) {
            this.connection = connection;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("POST".equals(exchange.getRequestMethod())) {
                JsonObject message = parseRequest(exchange);
                String cert_code = message.get("code").getAsString();

                // Machine 생성
                Machine machine = new Machine(null, connection, exchange);
                System.out.println("insert code api");
                machine.insertCode(cert_code);
            } else if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1); // No Content
            } else {
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            }
        }
    }

    static class ManageStockHandler implements HttpHandler {
        private final Connection connection;

        public ManageStockHandler(Connection connection) {
            this.connection = connection;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("POST".equals(exchange.getRequestMethod())) {
                JsonObject message = parseRequest(exchange);
                String item_code = message.get("item_code").getAsString();
                int item_num = message.get("item_num").getAsInt();
                // Machine 생성
                Machine machine = new Machine(null, connection, exchange);
                System.out.println("Manage Stock api");
                machine.editStock(item_code, item_num);
            } else if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1); // No Content
            } else {
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            }
        }
    }

    static class AddDVMHandler implements HttpHandler {
        private final Connection connection;

        public AddDVMHandler(Connection connection) {
            this.connection = connection;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("POST".equals(exchange.getRequestMethod())) {
                JsonObject message = parseRequest(exchange);
                String id = message.get("id").getAsString();
                String ip = message.get("ip").getAsString();
                String port = message.get("port").getAsString();

                // Machine 생성
                Machine machine = new Machine(null, connection, exchange);
                System.out.println("Add DVM api");
                machine.addDVM(id,ip,port);
            } else if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1); // No Content
            } else {
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            }
        }
    }

    static class RemoveDVMHandler implements HttpHandler {
        private final Connection connection;

        public RemoveDVMHandler(Connection connection) {
            this.connection = connection;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("POST".equals(exchange.getRequestMethod())) {
                JsonObject message = parseRequest(exchange);
                String id = message.get("id").getAsString();

                // Machine 생성
                Machine machine = new Machine(null, connection, exchange);
                System.out.println("Remove DVM api");
                machine.removeDVM(id);
            } else if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1); // No Content
            } else {
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            }
        }
    }

    private static JsonObject parseRequest(HttpExchange exchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        JsonObject message = JsonParser.parseReader(isr).getAsJsonObject();
        isr.close();
        return message;
    }
    private static void addCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "http://dvm-client.minboy.duckdns.org");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
        exchange.getResponseHeaders().add("Access-Control-Expose-Headers", "Content-Length,Content-Type");
        exchange.getResponseHeaders().add("Access-Control-Allow-Credentials", "true");
    }
}
