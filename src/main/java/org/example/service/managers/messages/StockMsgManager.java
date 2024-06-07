package org.example.service.managers.messages;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.example.model.dto.CoorDto;
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

    public CoorDto request(String id, String dst_id, int selected_code, int selected_num, JsonSocketService jsonRequestSocketService) {
        String StrItemCode = String.valueOf(selected_code);
        String result = "0" + StrItemCode;
        System.out.println("result : " + result);
        StockReqFormat req = new StockReqFormat(id, dst_id, StrItemCode, selected_num);
        JsonObject receivedMessage = sendMessage(req, jsonRequestSocketService);
        String msg_type = receivedMessage.get("msg_type").getAsString();
        JsonObject msg_content = receivedMessage.get("msg_content").getAsJsonObject();
        String item_code = msg_content.get("item_code").getAsString();
        int item_num = msg_content.get("item_num").getAsInt();

        if(item_num < selected_num || !msg_type.equals("resp_stock")) return null;

        int coor_x = msg_content.get("coor_x").getAsInt();
        int coor_y = msg_content.get("coor_y").getAsInt();
        return new CoorDto(dst_id, coor_x, coor_y, item_code);
    }

    public void response(String id, String dst_id, Object coor, int item_code, int item_num) {
        int stock_num = stockManager.checkStock(item_code, item_num);
        int[] coorArr = (int[])coor;
        String StrItemCode = String.valueOf(item_code);
        String result = "0" + StrItemCode;
        System.out.println("result : " + result);
        StockResFormat res = new StockResFormat(id, dst_id, result, stock_num, coorArr[0],coorArr[1]);
        sendMessage(res, this.jsonSocketService);
    }

    private JsonObject sendMessage(Object message, JsonSocketService SocketService){
        Gson gson = new Gson();
        String jsonStr = gson.toJson(message);
        JsonObject jsonObj = JsonParser.parseString(jsonStr).getAsJsonObject();
        SocketService.sendMessage(jsonObj);
        return SocketService.receiveMessage();
    }
}
