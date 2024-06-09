package org.example.service.managers.messages;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.example.model.dto.CoorDto;
import org.example.service.managers.PrintManager;
import org.example.service.managers.SaleManager;
import org.example.service.managers.StockManager;
import org.example.service.managers.messages.format.Prepayment.PrepaymentReqFormat;
import org.example.service.managers.messages.format.Prepayment.PrepaymentResFormat;
import org.example.service.managers.messages.format.Stock.StockReqFormat;
import org.example.service.socket.JsonSocketService;

public class PrepaymentMsgManager {
    private JsonSocketService jsonSocketService;
    private SaleManager saleManager;
    private StockManager stockManager;

    public PrepaymentMsgManager(JsonSocketService jsonSocketService, StockManager stockManager, SaleManager saleManager) {
        this.jsonSocketService = jsonSocketService;
        this.saleManager = saleManager;
        this.stockManager = stockManager;
    }

    public boolean request(String id, String dst_id, String selected_code, int selected_num, String cert_code, JsonSocketService jsonRequestSocketService) {
        PrepaymentReqFormat req = new PrepaymentReqFormat(id, dst_id, selected_code, selected_num, cert_code);
        Gson gson = new Gson();
        JsonObject receivedMessage = gson.fromJson(sendMessage(req, jsonRequestSocketService), JsonObject.class);

        String msg_type = receivedMessage.get("msg_type").getAsString();
        JsonObject msg_content = receivedMessage.get("msg_content").getAsJsonObject();
        String item_code = msg_content.get(("item_code")).getAsString();
        int item_num = msg_content.get("item_num").getAsInt();
        boolean availability = msg_content.get("availability").getAsBoolean();

        if(item_num < selected_num || !msg_type.equals("resp_prepay")) return false;
        return availability;
    }

    public void response(String id, String dst_id, String item_code, int item_num, String cert_code) {
        int stock_num = stockManager.checkStock(item_code, item_num);
        PrepaymentResFormat res;
        if(stock_num <= 0 || stock_num < item_num) {
            System.out.println("stock not enough to prepayment");
            res = new PrepaymentResFormat(id, dst_id, item_code, item_num, false);
        } else{
            System.out.println("stock enough to prepayment");
            res = new PrepaymentResFormat(id, dst_id, item_code, item_num, true);
            saleManager.processPrepayment(item_code, item_num, cert_code);
        }
        sendMessage(res, this.jsonSocketService);
    }

    private String sendMessage(Object message, JsonSocketService SocketService){
        Gson gson = new Gson();
        String jsonStr = gson.toJson(message);
        JsonObject jsonObj = JsonParser.parseString(jsonStr).getAsJsonObject();
        SocketService.sendMessage(jsonObj.toString());
        String receivedMessage = SocketService.receiveMessage();
        return receivedMessage != null ? receivedMessage.toString() : "";
    }
}
