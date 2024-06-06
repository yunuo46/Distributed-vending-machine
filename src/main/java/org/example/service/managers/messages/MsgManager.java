package org.example.service.managers.messages;

import org.example.service.managers.PrintManager;
import org.example.service.managers.SaleManager;
import org.example.service.managers.StockManager;
import org.example.service.socket.JsonSocketService;

public class MsgManager {
    private PrepaymentMsgManager prepaymentMsgManager;
    private StockMsgManager stockMsgManager;

    public MsgManager(JsonSocketService jsonSocketService, StockManager stockManager, PrintManager printManager, SaleManager saleManager) {
        prepaymentMsgManager = new PrepaymentMsgManager(jsonSocketService, stockManager, printManager, saleManager);
        stockMsgManager = new StockMsgManager(jsonSocketService, stockManager);
    }

    public void makeCode() {
        // TODO implement here
    }

    public void addDVM(String id, String ip, int port) {

    }

    public void removeDVM(String id) {
        // TODO implement here
    }

    public void stockResponse(String dst_id, String src_id, Object coordinate, int item_code, int item_num) {
        this.stockMsgManager.response(dst_id, src_id, coordinate, item_code, item_num);
    }

    public void prepaymentResponse(String dst_id, String src_id, int item_code, int item_num, String cert_code) {
        this.prepaymentMsgManager.response(dst_id, src_id, item_code, item_num, cert_code);
    }

    public void stockRequest(String id, int item_code, int item_num) {
    }
}
