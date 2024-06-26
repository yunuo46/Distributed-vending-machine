package org.example.service.socket;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class JsonSocketServiceImpl implements JsonSocketService {
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    private final Gson gson = new Gson();

    public JsonSocketServiceImpl(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void start() {
        try {
            this.writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
        } catch (Exception e) {
            throw new RuntimeException("Error initializing streams", e);
        }
    }

    @Override
    public void stop() {
        try {
            writer.close();
            reader.close();
            socket.close();
        } catch (Exception e) {
            throw new RuntimeException("Error closing streams", e);
        }
    }

    @Override
    public void sendMessage(String message) {
        System.out.println("sendMessage: " + message);
        writer.println(message);
    }

    @Override
    public String receiveMessage() {
        try {
            String received = reader.readLine().toString();
            JsonObject jsonObject = gson.fromJson(received, JsonObject.class);
            System.out.println("received message : " + jsonObject.toString());
            return jsonObject.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error receiving message", e);
        }
    }
}
