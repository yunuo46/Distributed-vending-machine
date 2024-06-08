package org.example.service.managers;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.example.model.dto.ClosestDVMDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PrintManagerTest {
    private HttpExchange exchange;
    private PrintManager printManager;
    private OutputStream outputStream;

    @BeforeEach
    public void setup() {
        exchange = mock(HttpExchange.class);
        outputStream = new ByteArrayOutputStream();
        when(exchange.getResponseBody()).thenReturn(outputStream);
        Headers headers = new Headers();
        when(exchange.getResponseHeaders()).thenReturn(headers);

        printManager = new PrintManager(exchange);
    }

    @Test
    public void testOfferItem() throws IOException {
        printManager.offerItem();

        verify(exchange, times(1)).sendResponseHeaders(eq(200), anyLong());
        assertResponseContains("{\"stock\":true,\"prepayment\":false}");
    }

    @Test
    public void testDisplayFailedGetItem() throws IOException {
        printManager.displayFailedGetItem();

        verify(exchange, times(1)).sendResponseHeaders(eq(200), anyLong());
        assertResponseContains("{\"stock\":false,\"prepayment\":false}");
    }

    @Test
    public void testDisplayPayment() throws IOException {
        printManager.displayPayment(true);

        verify(exchange, times(1)).sendResponseHeaders(eq(200), anyLong());
        assertResponseContains("{\"success\":true}");
    }

    @Test
    public void testDisplayClosestDVM() throws IOException {
        ClosestDVMDto closestDVM = new ClosestDVMDto("dvm1", 1, 1);
        printManager.displayClosestDVM(closestDVM);

        verify(exchange, times(1)).sendResponseHeaders(eq(200), anyLong());
        assertResponseContains("{\"stock\":false,\"prepayment\":true,\"dvm_id\":\"dvm1\",\"coor_x\":1,\"coor_y\":1}");
    }

    @Test
    public void testDisplayPrepayment() throws IOException {
        String certCode = "cert123";
        printManager.displayPrepayment(certCode);

        verify(exchange, times(1)).sendResponseHeaders(eq(200), anyLong());
        assertResponseContains("{\"success\":true,\"code\":\"cert123\"}");
    }

    @Test
    public void testDisplayNextDVM() throws IOException {
        String id = "dvm1";
        int x = 1;
        int y = 1;
        printManager.displayNextDVM(id, x, y);

        verify(exchange, times(1)).sendResponseHeaders(eq(200), anyLong());
        assertResponseContains("{\"success\":false,\"dvm_id\":\"dvm1\",\"x\":1,\"y\":1}");
    }

    @Test
    public void testDisplayInvalidCode() throws IOException {
        printManager.displayInvalidCode();

        verify(exchange, times(1)).sendResponseHeaders(eq(200), anyLong());
        assertResponseContains("{\"success\":false}");
    }

    @Test
    public void testDisplayValidCode() throws IOException {
        String itemCode = "item1";
        int itemNum = 1;
        printManager.displayValidCode(itemCode, itemNum);

        verify(exchange, times(1)).sendResponseHeaders(eq(200), anyLong());
        assertResponseContains("{\"success\":true,\"item_code\":\"item1\",\"item_num\":1}");
    }

    @Test
    public void testDisplayEditStock() throws IOException {
        printManager.displayEditStock(true);

        verify(exchange, times(1)).sendResponseHeaders(eq(200), anyLong());
        assertResponseContains("{\"success\":true}");
    }

    @Test
    public void testDisplayAddDVM() throws IOException {
        printManager.displayAddDVM();

        verify(exchange, times(1)).sendResponseHeaders(eq(200), anyLong());
        assertResponseContains("{\"success\":true}");
    }

    @Test
    public void testDisplayRemoveDVM() throws IOException {
        printManager.displayRemoveDVM(true);

        verify(exchange, times(1)).sendResponseHeaders(eq(200), anyLong());
        assertResponseContains("{\"success\":true}");
    }

    @Test
    public void testDisplayRefund() throws IOException {
        printManager.displayRefund();

        verify(exchange, times(1)).sendResponseHeaders(eq(200), anyLong());
        assertResponseContains("{\"success\":true}");
    }

    private void assertResponseContains(String expectedContent) throws IOException {
        outputStream.flush();
        String response = outputStream.toString();
        assertTrue(response.contains(expectedContent), "Response should contain: " + expectedContent);
    }
}
