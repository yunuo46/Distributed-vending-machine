package org.example.controller.managers.messages;

abstract class MsgManager {

    protected String cert_code;
    protected Object[] sortedDst;

    private void makeCode(){
        this.cert_code = "random_code";
    }

    abstract void convertToFormat(Object... params);
    abstract void request();
    abstract void response();
}
