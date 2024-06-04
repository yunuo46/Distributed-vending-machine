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
            Socket socket = new Socket("localhost", 8888);

            // 소켓을 이용하여 JsonSocketService 구현체 생성
            JsonSocketService jsonSocketService = new JsonSocketServiceImpl(socket);
            jsonSocketService.start();

            Gson gson = new Gson();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("msg_type", "req_stock");
            jsonObject.addProperty("src_id", "Team1");
            jsonObject.addProperty("dst_id", "Team9");

            JsonObject msg_content = new JsonObject();
            msg_content.addProperty("item_code", "02");
            msg_content.addProperty("item_num", "4");

            jsonObject.add("msg_content", msg_content);
            // 서버로 메시지 전송
            jsonSocketService.sendMessage(jsonObject);

            // 클라이언트 종료
            jsonSocketService.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
