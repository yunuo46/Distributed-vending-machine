package org.example.service.managers.messages;

import org.example.service.socket.JsonSocketService;

public class PrepaymentMsgManager extends MsgManager {
    private JsonSocketService jsonSocketService;

    public PrepaymentMsgManager(JsonSocketService jsonSocketService) {
        this.jsonSocketService = jsonSocketService;
    }

    public void request(String id, int selected_code, int selected_num, boolean option) {
        // TODO implement here
    }

    public void response(String id, String dst_id, Object coor, int item_code, int item_num, boolean availability) {
        // TODO implement here
    }

}
