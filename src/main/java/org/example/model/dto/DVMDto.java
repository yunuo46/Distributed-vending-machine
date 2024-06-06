package org.example.model.dto;

public class DVMDto {
    private String id;
    private String ip;
    private String port;

    public DVMDto(String id, String ip, String port) {
        this.id = id;
        this.ip = ip;
        this.port = port;
    }

    public String getId() {return id;}
    public String getIp() {return ip;}
    public String getPort() {return port;}
}
