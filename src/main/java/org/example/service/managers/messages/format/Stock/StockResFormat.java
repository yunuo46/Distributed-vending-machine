package org.example.service.managers.messages.format.Stock;

import org.example.service.managers.messages.format.MainFormat;

import java.util.HashMap;

public class StockResFormat extends MainFormat {
    public StockResFormat(String src_id, String dst_id, int item_code, int item_num, int coor_x, int coor_y) {
        this.msg_type = "resp_stock";
        this.src_id = src_id;
        this.dst_id = dst_id;

        this.msg_content = new HashMap<>();
        this.msg_content.put("item_code", item_code);
        this.msg_content.put("item_num", item_num);
        this.msg_content.put("coor_x", coor_x);
        this.msg_content.put("coor_y", coor_y);
    }
}
