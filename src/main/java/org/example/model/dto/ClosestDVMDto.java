package org.example.model.dto;

public class ClosestDVMDto {
    private String id;
    private int x;
    private int y;

    public ClosestDVMDto(String id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public String getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
