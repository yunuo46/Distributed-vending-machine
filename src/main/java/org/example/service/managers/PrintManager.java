package org.example.service.managers;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import org.example.model.dto.ClosestDVMDto;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class PrintManager {
    private HttpExchange exchange;

    public PrintManager(HttpExchange exchange) {
        this.exchange = exchange;
    }

    public void offerItem() {
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("stock", true);
        responseJson.addProperty("prepayment",false);
        sendResponse(responseJson);
    }

    public void displayFailedGetItem() {
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("stock", false);
        responseJson.addProperty("prepayment",false);
        sendResponse(responseJson);
    }

    public void displayPayment(boolean success) {
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("success", success);
        sendResponse(responseJson);
    }

    public void displayClosestDVM(ClosestDVMDto closestDVM) {
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("stock", false);
        responseJson.addProperty("prepayment",true);
        responseJson.addProperty("dvm_id", closestDVM.getId());
        responseJson.addProperty("coor_x", closestDVM.getX());
        responseJson.addProperty("coor_y", closestDVM.getY());
        sendResponse(responseJson);
    }

    public void displayPrepayment(String cert_code) {
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("success", true);
        responseJson.addProperty("code", cert_code);
        sendResponse(responseJson);
    }

    public void displayNextDVM(String id, int x, int y) {
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("success", false);
        responseJson.addProperty("dvm_id", id);
        responseJson.addProperty("x", x);
        responseJson.addProperty("y", y);
        sendResponse(responseJson);
    }

    public void displayInvalidCode() {
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("success", false);
        sendResponse(responseJson);
    }

    public void displayValidCode(String item_code, int item_num) {
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("success", true);
        responseJson.addProperty("item_code", item_code);
        responseJson.addProperty("item_num", item_num);
        sendResponse(responseJson);
    }

    public void displayEditStock(boolean success) {
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("success", success);
        sendResponse(responseJson);
    }

    public void displayAddDVM() {
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("success", true);
        sendResponse(responseJson);
    }

    public void displayRemoveDVM(boolean success) {
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("success", success);
        sendResponse(responseJson);
    }

    public void displayRefund() {
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("success", true);
        sendResponse(responseJson);
    }

    private void sendResponse(JsonObject jsonResponse) {
        try {
            String response = jsonResponse.toString();
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes(StandardCharsets.UTF_8));
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void displayLogin(boolean success) {
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("success", success);
        sendResponse(responseJson);
    }

    public void displayLogout() {
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("success", true);
        sendResponse(responseJson);
    }
}
