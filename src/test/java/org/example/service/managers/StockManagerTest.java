package org.example.service.managers;

import org.example.model.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class StockManagerTest {

    @Mock
    private Connection connection;

    @Mock
    private Stock stock;

    @InjectMocks
    private StockManager stockManager;

    @BeforeEach
    public void setup() throws NoSuchFieldException, IllegalAccessException {
        MockitoAnnotations.openMocks(this);
        stockManager = new StockManager(connection);
        Field stockField = StockManager.class.getDeclaredField("stock");
        stockField.setAccessible(true);
        stockField.set(stockManager, stock);
    }

    @Test
    public void testCheckStockWithSufficientStock() {
        String item_code = "item1";
        int item_num = 5;

        when(stock.checkStock(item_code)).thenReturn(10);

        int result = stockManager.checkStock(item_code, item_num);

        assertEquals(10, result);
    }

    @Test
    public void testCheckStockWithExcessiveRequest() {
        String item_code = "item1";
        int item_num = 15;

        int result = stockManager.checkStock(item_code, item_num);

        assertEquals(0, result);
    }

    @Test
    public void testSaleStock() {
        String item_code = "item1";
        int item_num = 5;

        stockManager.saleStock(item_code, item_num);

        verify(stock, times(1)).saleStock(item_code, item_num);
    }

    @Test
    public void testEditStockWithValidCode() {
        String item_code = "01";
        int item_num = 5;

        boolean result = stockManager.editStock(item_code, item_num);

        assertTrue(result);
        verify(stock, times(1)).editStock(item_code, item_num);
    }

    @Test
    public void testEditStockWithInvalidCode() {
        String item_code = "10";
        int item_num = 5;

        boolean result = stockManager.editStock(item_code, item_num);

        assertFalse(result);
        verify(stock, never()).editStock(anyString(), anyInt());
    }

    @Test
    public void testCheckPrice() {
        String item_code = "item1";
        int expectedPrice = 100;

        when(stock.checkPrice(item_code)).thenReturn(expectedPrice);

        int result = stockManager.checkPrice(item_code);

        assertEquals(expectedPrice, result);
    }
}
