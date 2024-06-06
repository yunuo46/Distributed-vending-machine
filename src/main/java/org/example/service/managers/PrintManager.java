package org.example.service.managers;
import org.example.service.socket.JsonSocketService;

public class PrintManager {
    private JsonSocketService jsonSocketService; ;

    public PrintManager(JsonSocketService jsonSocketService) {
        this.jsonSocketService = jsonSocketService;
    }

    /**
     * @param item_code
     * @param item_num
     */
    public void offerItem(int item_code, int item_num) {
        // TODO implement here
    }

    /**
     * @param coor
     * @param cert_code
     */
    public void offerCoorAndCode(Object coor, String cert_code) {
        // TODO implement here
    }

    /**
     * @param coor
     */
    public void offerCoor(Object coor) {
        // TODO implement here
    }

}