package org.example.service.managers.messages;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.example.service.managers.StockManager;
import org.example.service.managers.messages.format.Stock.StockReqFormat;
import org.example.service.managers.messages.format.Stock.StockResFormat;
import org.example.service.socket.JsonSocketService;
import org.example.service.socket.JsonSocketServiceImpl;

import java.net.Socket;

public class StockMsgManager {
    private JsonSocketService jsonSocketService;
    private StockManager stockManager;

    public StockMsgManager(JsonSocketService jsonSocketService, StockManager stockManager) {
        this.jsonSocketService = jsonSocketService;
        this.stockManager = stockManager;
    }

    public void connectSocket() {
        try {
            // 서버에 연결하기 위한 소켓 생성 및 서버의 IP 주소 및 포트 지정
            Socket socket = new Socket("localhost", 8888);

            // 소켓을 이용하여 JsonSocketService 구현체 생성
            JsonSocketService jsonSocketService = new JsonSocketServiceImpl(socket);
            jsonSocketService.start();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void request(String id, String dst_id, int selected_code, int selected_num) {
        StockReqFormat req = new StockReqFormat(id, dst_id, selected_code, selected_num);
        sendMessage(req);
    }

    public void response(String id, String dst_id, Object coor, int item_code, int item_num) {
        int stock_num = stockManager.checkStock(item_code, item_num);
        int[] coorArr = (int[])coor;
        StockResFormat res = new StockResFormat(id, dst_id, item_code, stock_num, coorArr[0],coorArr[1]);
        sendMessage(res);
    }

    private void sendMessage(Object message){
        Gson gson = new Gson();
        String jsonStr = gson.toJson(message);
        JsonObject jsonObj = JsonParser.parseString(jsonStr).getAsJsonObject();
        jsonSocketService.sendMessage(jsonObj);
    }
}
