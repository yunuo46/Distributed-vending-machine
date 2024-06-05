package org.example.controller;

import com.google.gson.JsonObject;
import org.example.service.managers.SaleManager;
import org.example.service.managers.StockManager;
import org.example.service.managers.messages.PrepaymentMsgManager;
import org.example.service.managers.messages.StockMsgManager;
import org.example.service.socket.JsonSocketService;

import java.sql.Connection;

public class Machine {
    public Machine(JsonSocketService jsonSocketService, Connection connection) {
        this.coordinate = new int[]{0, 0}; // 기본 좌표를 (0.0, 0.0)으로 초기화
        StockManager stockManager = new StockManager(connection);

        stockMsgManager = new StockMsgManager(jsonSocketService, stockManager);
        prepaymentMsgManager = new PrepaymentMsgManager(jsonSocketService);
    }

    private String id;
    private Object coordinate;
    private int selected_code;
    private int selected_num;
    private int selected_cost;
    private StockMsgManager stockMsgManager;
    private PrepaymentMsgManager prepaymentMsgManager;

    public void insertCode(String cert_code) {
        // TODO implement here
    }

    public void selectItem(int item_code, int item_num) {
        // TODO implement here
    }

    public void selectPaymentOption(boolean option) {
        // TODO implement here
    }

    public void insertCardData(String card_id) {
        // TODO implement here
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
        stockMsgManager.response(dst_id, src_id, coordinate, item_code, item_num);
    }

    public void prepaymentRequest(){
        //prepaymentMsgManager.request();
    }

    public void prepaymentResponse(){
        //prepaymentMsgManager.response();
    }
}