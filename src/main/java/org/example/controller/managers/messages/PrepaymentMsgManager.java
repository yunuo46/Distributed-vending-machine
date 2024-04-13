package org.example.controller.managers.messages;

import org.example.controller.managers.messages.format.Prepayment.PrepaymentReqFormat;
import org.example.controller.managers.messages.format.Prepayment.PrepaymentResFormat;

public class PrepaymentMsgManager extends MsgManager{

    private PrepaymentResFormat prepaymentResFormat;
    private PrepaymentReqFormat prepaymentReqFormat;

    @Override
    void convertToFormat(Object... params) {
        if(params[0] == "req_prepay") {
            // this.prepaymentReqFormat = new PrepaymentReqFormat()
            // format에 맞는 객체 생성하여 멤버 변수에 저장
        }else if(params[0] == "res_prepay") {
            // 이것도 객체 생성해서 저장..
        }else{
            // 에러 처리
        }
    }

    @Override
    void request(){
        // super.writer를 통해 request 전달
    }

    @Override
    void response(){

    }
}
