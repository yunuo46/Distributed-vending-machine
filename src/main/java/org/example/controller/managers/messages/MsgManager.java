package org.example.controller.managers.messages;

abstract class MsgManager {

    protected String cert_code;
    protected Object[] sortedDst;
    // writer를 지녀야함

    private void makeCode(){
        this.cert_code = "random_code";
    }

    abstract void request(String src_id, Object... massage_contents);
    abstract void response(String src_id, Object... massage_contents);
}
