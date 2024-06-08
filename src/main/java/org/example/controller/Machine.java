package org.example.controller;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import org.example.model.Card;
import org.example.model.DVM;
import org.example.model.PrepaymentState;
import org.example.model.SortedDVM;
import org.example.model.dto.ClosestDVMDto;
import org.example.model.dto.PrepaymentDto;
import org.example.service.managers.PrintManager;
import org.example.service.managers.SaleManager;
import org.example.service.managers.StockManager;
import org.example.service.managers.messages.MsgManager;
import org.example.service.socket.JsonSocketService;

import java.io.IOException;
import java.sql.Connection;

public class Machine {
    private final String id;
    private final int[] coordinate;
    private final MsgManager msgManager;
    private final StockManager stockManager;
    private final SaleManager saleManager;
    private final PrintManager printManager;
    private final DVM dvm;
    private final SortedDVM sortedDvm;

    public Machine(JsonSocketService jsonSocketService, Connection connection, HttpExchange exchange) {
        id = System.getenv("MACHINE_ID");
        coordinate = new int[]{Integer.parseInt(System.getenv("X")), Integer.parseInt(System.getenv("Y"))}; // 기본 좌표를 (0, 0)으로 초기화
        printManager = new PrintManager(exchange);
        dvm = new DVM(connection);
        sortedDvm = new SortedDVM(connection);
        stockManager = new StockManager(connection);

        PrepaymentState prepaymentState = new PrepaymentState(connection);
        Card card = new Card(connection);

        this.saleManager = new SaleManager(stockManager, printManager, prepaymentState, card);
        this.msgManager = new MsgManager(jsonSocketService, stockManager, saleManager, dvm, sortedDvm, coordinate);

        System.out.println("Machine Info: "+ this.id + "{" +this.coordinate[0]+", "+this.coordinate[1] + "}");
    }

    public void insertCode(String cert_code) {
        saleManager.offerPrepaidItem(cert_code);
    }

    public void selectItem(String item_code, int item_num) {
        boolean isStock = saleManager.offerItem(item_code, item_num);
        if(!isStock){
            System.out.println("stock not enough to payment");
            ClosestDVMDto closestDVM = msgManager.stockRequest(id, item_code, item_num);
            if(closestDVM == null){
                System.out.println("No DVM exists with sufficient stock");
                printManager.displayFailedGetItem();
            }
            else {
                System.out.println("closest DVM: " + closestDVM.getId());
                printManager.displayClosestDVM(closestDVM);
            }
        }
    }

    public void ProcessPrepayment(String dst_id, String item_code, int item_num) throws IOException {
        PrepaymentDto prepaymentDto = msgManager.prepaymentRequest(id, dst_id, item_code, item_num);
        boolean success = prepaymentDto.isSuccess();
        if(success){
            System.out.println("prepayment successful");
            sortedDvm.removeRemainSortedDVM(item_code);
            printManager.displayPrepayment(prepaymentDto.getCertCode());
        }else{
            ClosestDVMDto closestDVMDto = sortedDvm.getNearestDVM(item_code);
            if(closestDVMDto == null){
                System.out.println("prepayment failed");
                printManager.displayNextDVM(null,0,0);
            }
            else {
                System.out.println("prepayment next DVM: " + closestDVMDto.getId());
                printManager.displayNextDVM(closestDVMDto.getId(), closestDVMDto.getX(), closestDVMDto.getY());
            }
        }
    }

    public void insertCardData(String card_id, String item_code, int item_num, boolean isPrepay) {
        saleManager.checkCardData(card_id, item_code,item_num, isPrepay);
    }

    public void stockResponse(JsonObject message){
        String src_id = message.get("src_id").getAsString();
        String dst_id = message.get("dst_id").getAsString();
        JsonObject msg_content = message.get("msg_content").getAsJsonObject();
        String item_code = msg_content.get("item_code").getAsString();
        int item_num = msg_content.get("item_num").getAsInt();
        msgManager.stockResponse(dst_id, src_id, coordinate, item_code, item_num);
    }

    public void prepaymentResponse(JsonObject message){
        String src_id = message.get("src_id").getAsString();
        String dst_id = message.get("dst_id").getAsString();
        JsonObject msg_content = message.get("msg_content").getAsJsonObject();
        String item_code = msg_content.get("item_code").getAsString();
        int item_num = msg_content.get("item_num").getAsInt();
        String cert_code = msg_content.get("cert_code").getAsString();
        msgManager.prepaymentResponse(dst_id, src_id, item_code, item_num, cert_code);
    }

    public void editStock(String item_code, int item_num) {
        stockManager.editStock(item_code, item_num);
        printManager.displayEditStock();
    }

    public void addDVM(String id, String ip, String port) {
        dvm.addDVM(id, ip, port);
        printManager.displayAddDVM();
    }

    public void removeDVM(String id) {
        boolean success = dvm.removeDVM(id);
        printManager.displayRemoveDVM(success);
    }

    public void refundPrepayment(String card_data, String item_code, int item_num) {
        saleManager.refundPrepayment(card_data, item_code, item_num);
    }
}