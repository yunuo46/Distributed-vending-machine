package org.example.service.managers;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;

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

    public void offerCoorAndCode(Object coor, String cert_code) {
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("coor", coor.toString()); // assuming coor has a meaningful toString() method
        responseJson.addProperty("cert_code", cert_code);
        sendResponse(responseJson);
    }

    public void offerCoor(Object coor) {
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("coor", coor.toString()); // assuming coor has a meaningful toString() method
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
}
