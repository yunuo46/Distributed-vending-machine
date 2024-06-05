package org.example.service.managers;

import org.example.model.Stock;

import java.sql.Connection;

public class StockManager {
    private Stock stock;

    public StockManager(Connection connection) {
        this.stock = new Stock(connection);
    }

    private int total_stock;
    private int MAX_CNT;


    public int checkStock(int item_code, int item_num) {
        if(MAX_CNT >= item_num) return 0;
        else return stock.checkStock(item_code);
    }

    public void saleStock(int item_code, int item_num) {
        // TODO implement here
    }

    /**
     * @param item_code
     * @param item_num
     */
    public void editStock(int item_code, int item_num) {
        // TODO implement here
    }

}
