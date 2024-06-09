package org.example.controller;

import com.sun.net.httpserver.HttpExchange;
import org.example.model.DVM;
import org.example.service.managers.PrintManager;
import org.example.service.managers.StockManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;

import static org.mockito.Mockito.*;

class AdminTest {

    @Mock
    private Connection connection;

    @Mock
    private HttpExchange exchange;

    @Mock
    private PrintManager printManager;

    @Mock
    private StockManager stockManager;

    @Mock
    private DVM dvm;

    @InjectMocks
    private Admin admin;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        admin = new Admin(connection, exchange);

        // Use reflection to inject mocks into the Admin instance
        injectMocks(admin, "printManager", printManager);
        injectMocks(admin, "stockManager", stockManager);
        injectMocks(admin, "dvm", dvm);
    }

    @Test
    void testLoginSuccess() {
        admin.login("admin", "admin");
        verify(printManager, times(1)).displayLogin(true);
    }

    @Test
    void testLoginFailure() {
        admin.login("admin", "wrongpassword");
        verify(printManager, times(1)).displayLogin(false);
    }

    @Test
    void testLogout() {
        admin.logout();
        verify(printManager, times(1)).displayLogout();
    }

    @Test
    void testEditStock() {
        String item_code = "item1";
        int item_num = 10;

        when(stockManager.editStock(item_code, item_num)).thenReturn(true);

        admin.editStock(item_code, item_num);

        verify(stockManager, times(1)).editStock(item_code, item_num);
        verify(printManager, times(1)).displayEditStock(true);
    }

    @Test
    void testAddDVM() {
        String id = "dvm1";
        String ip = "192.168.1.1";
        String port = "8080";

        admin.addDVM(id, ip, port);

        verify(dvm, times(1)).addDVM(id, ip, port);
        verify(printManager, times(1)).displayAddDVM();
    }

    @Test
    void testRemoveDVM() {
        String id = "dvm1";

        when(dvm.removeDVM(id)).thenReturn(true);

        admin.removeDVM(id);

        verify(dvm, times(1)).removeDVM(id);
        verify(printManager, times(1)).displayRemoveDVM(true);
    }

    // Helper method to inject mocks using reflection
    private void injectMocks(Object target, String fieldName, Object mock) {
        try {
            java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, mock);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
