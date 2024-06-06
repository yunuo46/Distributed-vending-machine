package org.example.controller;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import org.example.model.Card;
import org.example.model.PrepaymentState;
import org.example.service.managers.PrintManager;
import org.example.service.managers.SaleManager;
import org.example.service.managers.StockManager;
import org.example.service.managers.messages.MsgManager;
import org.example.service.socket.JsonSocketService;

import java.sql.Connection;

public class Machine {
    private String id = "team9";
    private Object coordinate;
    private MsgManager msgManager;
    private SaleManager saleManager;

    public Machine(JsonSocketService jsonSocketService, Connection connection, HttpExchange exchange) {
        this.coordinate = new int[]{0, 0}; // 기본 좌표를 (0, 0)으로 초기화

        StockManager stockManager = new StockManager(connection);
        PrintManager printManager = new PrintManager(exchange);
        PrepaymentState prepaymentState = new PrepaymentState(connection);
        Card card = new Card(connection);

        this.saleManager = new SaleManager(stockManager, printManager, prepaymentState, card);
        this.msgManager = new MsgManager(jsonSocketService, stockManager, printManager, this.saleManager);
    }

    public void insertCode(String cert_code) {
        // TODO implement here
    }

    public void selectItem(int item_code, int item_num) {
        saleManager.offerItem(item_code, item_num);
    }

    public void selectPaymentOption(boolean option) {
        // TODO implement here
    }

    public void insertCardData(String card_id, int item_code, int item_num) {
        saleManager.checkCardData(card_id, item_code,item_num);
    }

    public void stockRequest(JsonObject message){
        //stockMsgManager.request();
    }

    public void stockResponse(JsonObject message){
        String src_id = message.get("src_id").getAsString();
        String dst_id = message.get("dst_id").getAsString();
        JsonObject msg_content = message.get("msg_content").getAsJsonObject();
        int item_code = msg_content.get("item_code").getAsInt();
        int item_num = msg_content.get("item_num").getAsInt();
        msgManager.stockResponse(dst_id, src_id, coordinate, item_code, item_num);
    }

    public void prepaymentRequest(){
        //prepaymentMsgManager.request();
    }

    public void prepaymentResponse(JsonObject message){
        String src_id = message.get("src_id").getAsString();
        String dst_id = message.get("dst_id").getAsString();
        JsonObject msg_content = message.get("msg_content").getAsJsonObject();
        int item_code = msg_content.get("item_code").getAsInt();
        int item_num = msg_content.get("item_num").getAsInt();
        String cert_code = msg_content.get("cert_code").getAsString();
        msgManager.prepaymentResponse(dst_id, src_id, item_code, item_num, cert_code);
    }
}