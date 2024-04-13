package org.example.controller.managers.messages;

import org.example.controller.managers.messages.format.Prepayment.PrepaymentReqFormat;
import org.example.controller.managers.messages.format.Prepayment.PrepaymentResFormat;

public class PrepaymentMsgManager extends MsgManager{

    @Override
    void request(String src_id, Object... massage_contents){
        // super.writer를 통해 request 전달
    }

    @Override
    void response(String src_id, Object... massage_contents){

    }
}
