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
            Socket socket = new Socket("34.22.98.249",8888);

            // 소켓을 이용하여 JsonSocketService 구현체 생성
            JsonSocketService jsonSocketService = new JsonSocketServiceImpl(socket);
            jsonSocketService.start();

            // Example 1 (request Stock)
            String jsonString = "{\"src_id\":\"Team11\",\"msg_content\":{\"item_code\":\"1\",\"item_num\":\"1\"},\"msg_type\":\"req_stock\",\"dst_id\":\"Team10\"}";
            jsonSocketService.sendMessage(jsonString);
            jsonSocketService.receiveMessage();

            // 클라이언트 종료
            jsonSocketService.stop();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
