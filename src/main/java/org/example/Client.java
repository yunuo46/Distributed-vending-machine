package org.example;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.example.service.socket.JsonSocketService;
import org.example.service.socket.JsonSocketServiceImpl;

import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        try {
            // 서버에 연결하기 위한 소켓 생성 및 서버의 IP 주소 및 포트 지정
            Socket socket = new Socket("localhost",8888);

            // 소켓을 이용하여 JsonSocketService 구현체 생성
            JsonSocketService jsonSocketService = new JsonSocketServiceImpl(socket);
            jsonSocketService.start();

            // Example 1 (request Stock)
            /*
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("msg_type", "req_stock");
            jsonObject.addProperty("src_id", "Team1");
            jsonObject.addProperty("dst_id", "Team9");

            JsonObject msg_content = new JsonObject();
            msg_content.addProperty("item_code", "02");
            msg_content.addProperty("item_num", "5");

            jsonObject.add("msg_content", msg_content);

            jsonSocketService.sendMessage(jsonObject);

            JsonObject receivedMessage = jsonSocketService.receiveMessage(JsonObject.class);
            System.out.println(receivedMessage);
            */

            // Example 2 (request Prepayment)
            JsonObject jsonObject2 = new JsonObject();
            jsonObject2.addProperty("msg_type", "req_prepayment");
            jsonObject2.addProperty("src_id", "Team1");
            jsonObject2.addProperty("dst_id", "Team9");

            JsonObject msg_content2 = new JsonObject();
            msg_content2.addProperty("item_code", "02");
            msg_content2.addProperty("item_num", "3");
            msg_content2.addProperty("cert_code", "test_code");

            jsonObject2.add("msg_content", msg_content2);

            jsonSocketService.sendMessage(jsonObject2);

            JsonObject receivedMessage2 = jsonSocketService.receiveMessage(JsonObject.class);
            System.out.println(receivedMessage2);

            // 클라이언트 종료
            jsonSocketService.stop();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
