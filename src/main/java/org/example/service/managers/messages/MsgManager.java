package org.example.service.managers.messages;

import org.example.model.DVM;
import org.example.model.SortedDVM;
import org.example.model.dto.ClosestDVMDto;
import org.example.model.dto.CoorDto;
import org.example.model.dto.DVMDto;
import org.example.model.dto.PrepaymentDto;
import org.example.service.managers.SaleManager;
import org.example.service.managers.StockManager;
import org.example.service.socket.JsonSocketService;
import org.example.service.socket.JsonSocketServiceImpl;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MsgManager {
    private PrepaymentMsgManager prepaymentMsgManager;
    private StockMsgManager stockMsgManager;
    private final DVM dvm;
    private final SortedDVM sortedDVM;
    private final int[] coordinate;

    public MsgManager(JsonSocketService jsonSocketService, StockManager stockManager, SaleManager saleManager, DVM dvm, SortedDVM sortedDVM, int[] coordinate) {
        prepaymentMsgManager = new PrepaymentMsgManager(jsonSocketService, stockManager, saleManager);
        stockMsgManager = new StockMsgManager(jsonSocketService, stockManager);
        this.dvm = dvm;
        this.sortedDVM = sortedDVM;
        this.coordinate = coordinate;
    }

    public String makeCode() {
        String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        int CODE_LENGTH = 10;
        Random RANDOM = new Random();

        StringBuilder authCode = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            authCode.append(CHARACTERS.charAt(index));
        }
        return authCode.toString();
    }

    public void addDVM(String id, String ip, String port) {
        dvm.addDVM(id,ip,port);
    }

    public void removeDVM(String id) {
        dvm.removeDVM(id);
    }

    public void stockResponse(String dst_id, String src_id, Object coordinate, int item_code, int item_num) {
        this.stockMsgManager.response(dst_id, src_id, coordinate, item_code, item_num);
    }

    public void prepaymentResponse(String dst_id, String src_id, int item_code, int item_num, String cert_code) {
        this.prepaymentMsgManager.response(dst_id, src_id, item_code, item_num, cert_code);
    }

    public ClosestDVMDto stockRequest(String id, int item_code, int item_num) {
        List<DVMDto> dvmList = dvm.getAllDVM();
        int x = this.coordinate[0];
        int y = this.coordinate[1];

        List<CoorDto> coors = new ArrayList<>();

        for (DVMDto dvm : dvmList) {
            try {
                System.out.println("stock request to "+dvm.getId());
                JsonSocketService JsonRequestSocketService = connectSocket(dvm.getIp(),Integer.parseInt(dvm.getPort()));
                CoorDto coor = stockMsgManager.request(id, dvm.getId(), item_code, item_num, JsonRequestSocketService);
                if(coor != null) {
                    System.out.println(dvm.getId()+" have sufficient stock");
                    coors.add(coor);
                }
                JsonRequestSocketService.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (CoorDto coor : coors) {
            String dvm_id = coor.getDstId();
            int dvm_x = coor.getCoorX();
            int dvm_y = coor.getCoorY();
            String dvm_item_code = coor.getItemCode();
            float dist = (float)Math.sqrt(Math.pow(dvm_x-x,2)+Math.pow(dvm_y-y,2));
            System.out.println("dvm_id: "+dvm_id+"dist: "+dist);
            sortedDVM.addSortedDVM(dvm_id, dvm_x, dvm_y,dvm_item_code, dist);
        }
        return sortedDVM.getNearestDVM(item_code);
    }

    public PrepaymentDto prepaymentRequest(String id, String dst_id, int item_code, int item_num) throws IOException {
        String cert_code = makeCode();
        DVMDto dvmIpPort = dvm.getIpPort(dst_id);
        String ip = dvmIpPort.getIp();
        int port = Integer.parseInt(dvmIpPort.getPort());
        JsonSocketService jsonRequestSocketService = connectSocket(ip,port);
        boolean success = prepaymentMsgManager.request(id ,dst_id, item_code, item_num, cert_code, jsonRequestSocketService);
        return new PrepaymentDto(success, cert_code);
    }

    private JsonSocketService connectSocket(String ip, int port) throws IOException {
        Socket socket = new Socket(ip, port);
        JsonSocketService jsonSocketService = new JsonSocketServiceImpl(socket);
        jsonSocketService.start();
        return jsonSocketService;
    }
}
