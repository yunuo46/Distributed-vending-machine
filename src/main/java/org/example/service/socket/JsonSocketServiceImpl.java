package org.example.service.socket;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

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
    public void sendMessage(JsonObject message) {
        System.out.println(message);
        System.out.println(gson.toJson(message));
        writer.println(gson.toJson(message));
    }

    @Override
    public <T> T receiveMessage(Class<T> clazz) {
        try {
            System.out.println(reader.readLine());
            System.out.println("gson.fromJson : " + gson.fromJson(reader.readLine(), clazz));
            return gson.fromJson(reader.readLine(), clazz);
        } catch (Exception e) {
            throw new RuntimeException("Error receiving message", e);
        }
    }
}
