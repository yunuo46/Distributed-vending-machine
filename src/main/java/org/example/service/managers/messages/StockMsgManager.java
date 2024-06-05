package org.example.service.managers.messages;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.example.service.managers.StockManager;
import org.example.service.managers.messages.format.Stock.StockResFormat;
import org.example.service.socket.JsonSocketService;

public class StockMsgManager extends MsgManager {
    private JsonSocketService jsonSocketService;
    private StockManager stockManager;

    public StockMsgManager(JsonSocketService jsonSocketService, StockManager stockManager) {
        this.jsonSocketService = jsonSocketService;
        this.stockManager = stockManager;
    }

    public void request(String id, int selected_code, int selected_num) {
        // TODO implement here
    }

    public void response(String id, String dst_id, Object coor, int item_code, int item_num) {
        int stock_num = stockManager.checkStock(item_code, item_num);
        int[] coorArr = (int[])coor;
        StockResFormat res = new StockResFormat(id, dst_id, item_code, stock_num, coorArr[0],coorArr[1]);
        Gson gson = new Gson();
        String jsonStr = gson.toJson(res);
        JsonObject jsonObj = JsonParser.parseString(jsonStr).getAsJsonObject();
        jsonSocketService.sendMessage(jsonObj);
    }
}
