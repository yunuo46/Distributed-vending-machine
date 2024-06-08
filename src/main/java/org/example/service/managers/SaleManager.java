package org.example.service.managers;

import org.example.model.Card;
import org.example.model.PrepaymentState;

import java.util.Map;

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

    public void processPrepayment(String item_code, int item_num, String cert_code) {
        System.out.println("sale stock and store prepayment");
        stockManager.saleStock(item_code, item_num);
        prepaymentState.storePrePayment(item_code, item_num, cert_code);
    }

    public void offerPrepaidItem(String cert_code) {
        Map<String, String> map = prepaymentState.checkCode(cert_code);
        String item_code = map.get("item_code");
        if(item_code == "0") printManager.displayInvalidCode();
        else {
            int item_num = Integer.parseInt(map.get("item_num"));
            printManager.displayValidCode(item_code,item_num);
        }
    }

    public boolean offerItem(String selected_code, int selected_num) {
        int stock_num = stockManager.checkStock(selected_code, selected_num);
        if(stock_num > 0 && stock_num >= selected_num) {
            printManager.offerItem();
            return true;
        }else return false;
    }

    public void checkCardData(String card_id, String item_code, int item_num) {
        int price = item_num * stockManager.checkPrice(item_code);
        System.out.println("price is " + price);
        boolean success = card.checkCardData(card_id, price);
        if(success) {
            System.out.println("card is valid");
            stockManager.saleStock(item_code, item_num);
        }
        printManager.displayPayment(success);
    }
}
