package org.example.controller;

import com.sun.net.httpserver.HttpExchange;
import org.example.model.DVM;
import org.example.model.SortedDVM;
import org.example.service.managers.PrintManager;
import org.example.service.managers.StockManager;

import java.sql.Connection;

public class Admin {
    private String id = "admin";
    private String pwd = "admin";
    private final PrintManager printManager;
    private final StockManager stockManager;
    private final DVM dvm;

    public Admin(Connection connection, HttpExchange exchange) {
        printManager = new PrintManager(exchange);
        dvm = new DVM(connection);
        stockManager = new StockManager(connection);
    }

    public void login(String id, String pwd) {
        if (this.id.equals(id) && this.pwd.equals(pwd)) printManager.displayLogin(true);
         else printManager.displayLogin(false);
    }

    public void logout() {
        printManager.displayLogout();
    }

    public void editStock(String item_code, int item_num) {
        boolean success = stockManager.editStock(item_code, item_num);
        printManager.displayEditStock(success);
    }

    public void addDVM(String id, String ip, String port) {
        dvm.addDVM(id, ip, port);
        printManager.displayAddDVM();
    }

    public void removeDVM(String id) {
        boolean success = dvm.removeDVM(id);
        printManager.displayRemoveDVM(success);
    }
}


