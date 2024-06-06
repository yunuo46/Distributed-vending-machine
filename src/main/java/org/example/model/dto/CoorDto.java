package org.example.model.dto;

public class CoorDto {
    private String dst_id;
    private int coor_x;
    private int coor_y;
    private int item_code;

    public CoorDto(String dst_id, int coor_x, int coor_y, int item_code) {
        this.dst_id = dst_id;
        this.coor_x = coor_x;
        this.coor_y = coor_y;
        this.item_code = item_code;
    }

    public String getDstId() {
        return dst_id;
    }

    public int getCoorX() {
        return coor_x;
    }

    public int getCoorY() {
        return coor_y;
    }

    public int getItemCode() {
        return item_code;
    }
}
