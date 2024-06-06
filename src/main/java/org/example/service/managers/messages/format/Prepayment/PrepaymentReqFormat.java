package org.example.service.managers.messages.format.Prepayment;

import org.example.service.managers.messages.format.MainFormat;

import java.util.HashMap;

public class PrepaymentReqFormat extends MainFormat {
    public PrepaymentReqFormat(String src_id, String dst_id, String item_code, int item_num, String cert_code) {
        this.msg_type = "req_prepay";
        this.src_id = src_id;
        this.dst_id = dst_id;

        this.msg_content = new HashMap<>();
        this.msg_content.put("item_code", item_code);
        this.msg_content.put("item_num", item_num);
        this.msg_content.put("cert_code", cert_code);
    }
}
