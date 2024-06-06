package org.example.service.managers.messages;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.example.service.managers.PrintManager;
import org.example.service.managers.SaleManager;
import org.example.service.managers.StockManager;
import org.example.service.managers.messages.format.Prepayment.PrepaymentResFormat;
import org.example.service.socket.JsonSocketService;

public class PrepaymentMsgManager {
    private JsonSocketService jsonSocketService;
    private PrintManager printManager;
    private SaleManager saleManager;
    private StockManager stockManager;

    public PrepaymentMsgManager(JsonSocketService jsonSocketService, StockManager stockManager, PrintManager printManager, SaleManager saleManager) {
        this.jsonSocketService = jsonSocketService;
        this.printManager = printManager;
        this.saleManager = saleManager;
        this.stockManager = stockManager;
    }

    public void request(String id, int selected_code, int selected_num, boolean option) {
        // TODO implement here
    }

    public void response(String id, String dst_id, int item_code, int item_num, String cert_code) {
        int stock_num = stockManager.checkStock(item_code, item_num);
        PrepaymentResFormat res;
        if(stock_num == 0 || stock_num < item_num) {
            res = new PrepaymentResFormat(id, dst_id, item_code, item_num, false);
        } else{
            res = new PrepaymentResFormat(id, dst_id, item_code, item_num, true);
            saleManager.processPrepayment(item_code, item_num, cert_code);
        }
        sendMessage(res);
    }

    private void sendMessage(Object message){
        Gson gson = new Gson();
        String jsonStr = gson.toJson(message);
        JsonObject jsonObj = JsonParser.parseString(jsonStr).getAsJsonObject();
        jsonSocketService.sendMessage(jsonObj);
    }
}
