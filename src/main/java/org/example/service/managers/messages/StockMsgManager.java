package org.example.service.managers.messages;

import org.example.service.managers.StockManager;
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
        boolean isStock = stockManager.checkStock(item_code, item_num);
        if(isStock){
            // 재고 존재 응답
        }else{
            // 재고 없음 응답
        }
    }
}
