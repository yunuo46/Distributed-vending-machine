package org.example.service.managers.messages.format.Prepayment;

import org.example.service.managers.messages.format.MainFormat;

import java.util.HashMap;

public class PrepaymentResFormat extends MainFormat {
    public PrepaymentResFormat(String src_id, String dst_id, String item_code, int item_num, boolean availability) {
        this.msg_type = "resp_prepay";
        this.src_id = src_id;
        this.dst_id = dst_id;

        this.msg_content = new HashMap<>();
        this.msg_content.put("item_code", item_code);
        this.msg_content.put("item_num", item_num);
        this.msg_content.put("availability", availability);
    }
}
