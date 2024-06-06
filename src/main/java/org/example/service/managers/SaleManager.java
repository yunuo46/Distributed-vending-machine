package org.example.service.managers;

import org.example.model.Card;
import org.example.model.PrepaymentState;

public class SaleManager {
    private int total_sales;
    private StockManager stockManager;
    private PrepaymentState prepaymentState;
    private Card card;

    public SaleManager(StockManager stockManager, PrepaymentState prepaymentState, Card card) {
        this.stockManager = stockManager;
        this.prepaymentState = prepaymentState;
        this.card = card;
    }

    public void processPrepayment(int item_code, int item_num, String cert_code) {
        stockManager.saleStock(item_code, item_num);
        prepaymentState.storePrePayment(item_code, item_num, cert_code);
    }

    public void offerPrepaidItem(String cert_code) {
        // TODO implement here
    }

    public void offerItem(int selected_code, int selected_num) {
        int stock_num = stockManager.checkStock(selected_code, selected_num);
        System.out.println(stock_num);
    }

    public void checkCardData(String card_id, int price) {
        // TODO implement here
    }
}
