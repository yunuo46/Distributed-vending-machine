package org.example.service.socket;

import com.google.gson.JsonObject;

public interface JsonSocketService {
    void start();
    void stop();
    void sendMessage(JsonObject message);
    JsonObject receiveMessage();
}
