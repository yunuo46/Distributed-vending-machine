package org.example.service.socket;

public interface JsonSocketService {
    void start();
    void stop();
    void sendMessage(Object message);
    <T> T receiveMessage(Class<T> clazz);
}
