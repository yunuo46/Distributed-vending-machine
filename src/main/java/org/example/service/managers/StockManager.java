package org.example.service.managers;

import org.example.model.Stock;

import java.sql.Connection;

public class StockManager {
    private Stock stock;

    public StockManager(Connection connection) {
        this.stock = new Stock(connection);
    }

    private int total_stock;
    private int MAX_CNT = 10;


    public int checkStock(String item_code, int item_num) {
        if(MAX_CNT < item_num) return 0;
        else return stock.checkStock(item_code);
    }

    public void saleStock(String item_code, int item_num) {
        stock.saleStock(item_code, item_num);
    }

    public void editStock(String item_code, int item_num) {
        stock.editStock(item_code, item_num);
    }

    public int checkPrice(String item_code) {
        return stock.checkPrice(item_code);
    }
}
