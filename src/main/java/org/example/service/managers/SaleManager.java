package org.example.service.managers;

import org.example.model.Card;
import org.example.model.PrepaymentState;

public class SaleManager {
    private int total_sales;
    private StockManager stockManager;
    private PrintManager printManager;
    private PrepaymentState prepaymentState;
    private Card card;

    public SaleManager(StockManager stockManager, PrintManager printManager, PrepaymentState prepaymentState, Card card) {
        this.stockManager = stockManager;
        this.printManager = printManager;
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

    public boolean offerItem(int selected_code, int selected_num) {
        int stock_num = stockManager.checkStock(selected_code, selected_num);
        if(stock_num > 0) {
            printManager.offerItem();
            return true;
        }else return false;
    }

    public void checkCardData(String card_id, int item_code, int item_num) {
        int price = item_num * stockManager.checkPrice(item_code);
        boolean success = card.checkCardData(card_id, price);
        if(success) {
            stockManager.saleStock(item_code, item_num);
        }
        printManager.displayPayment(success);
    }
}
