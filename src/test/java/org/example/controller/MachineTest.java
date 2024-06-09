package org.example.controller;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import org.example.model.DVM;
import org.example.model.SortedDVM;
import org.example.model.dto.PrepaymentDto;
import org.example.service.managers.PrintManager;
import org.example.service.managers.SaleManager;
import org.example.service.managers.StockManager;
import org.example.service.managers.messages.MsgManager;
import org.example.service.socket.JsonSocketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.SetEnvironmentVariable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.sql.Connection;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SetEnvironmentVariable(key = "MACHINE_ID", value = "Team9")
@SetEnvironmentVariable(key = "X", value = "0")
@SetEnvironmentVariable(key = "Y", value = "0")
class MachineTest {

    @Mock
    private JsonSocketService jsonSocketService;

    @Mock
    private Connection connection;

    @Mock
    private HttpExchange exchange;

    @Mock
    private PrintManager printManager;

    @Mock
    private StockManager stockManager;

    @Mock
    private SaleManager saleManager;

    @Mock
    private MsgManager msgManager;

    @Mock
    private DVM dvm;

    @Mock
    private SortedDVM sortedDVM;

    @InjectMocks
    private Machine machine;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Machine 인스턴스 생성
        machine = new Machine(jsonSocketService, connection, exchange);

        // Use reflection to inject mocks into the Machine instance
        injectMocks(machine, "printManager", printManager);
        injectMocks(machine, "stockManager", stockManager);
        injectMocks(machine, "saleManager", saleManager);
        injectMocks(machine, "msgManager", msgManager);
        injectMocks(machine, "dvm", dvm);
        injectMocks(machine, "sortedDvm", sortedDVM);
    }

    @Test
    void testInsertCode() {
        String cert_code = "CERT123";
        machine.insertCode(cert_code);
        verify(saleManager, times(1)).offerPrepaidItem(cert_code);
    }

    @Test
    void testSelectItem_StockAvailable() {
        String item_code = "ITEM1";
        int item_num = 10;

        when(saleManager.offerItem(item_code, item_num)).thenReturn(true);

        machine.selectItem(item_code, item_num);

        verify(saleManager, times(1)).offerItem(item_code, item_num);
        verify(printManager, never()).displayFailedGetItem();
    }

    @Test
    void testSelectItem_StockUnavailable() {
        String item_code = "ITEM1";
        int item_num = 10;

        when(saleManager.offerItem(item_code, item_num)).thenReturn(false);
        when(msgManager.stockRequest(anyString(), eq(item_code), eq(item_num))).thenReturn(null);

        machine.selectItem(item_code, item_num);

        verify(saleManager, times(1)).offerItem(item_code, item_num);
        verify(printManager, times(1)).displayFailedGetItem();
    }

    @Test
    void testProcessPrepayment_Success() throws IOException {
        String dst_id = "DST1";
        String item_code = "ITEM1";
        int item_num = 10;

        PrepaymentDto prepaymentDto = new PrepaymentDto(true, "CERT123");
        when(msgManager.prepaymentRequest(anyString(), eq(dst_id), eq(item_code), eq(item_num))).thenReturn(prepaymentDto);

        machine.ProcessPrepayment(dst_id, item_code, item_num);

        verify(printManager, times(1)).displayPrepayment("CERT123");
        verify(sortedDVM, times(1)).removeRemainSortedDVM(item_code);
    }

    @Test
    void testProcessPrepayment_Failure() throws IOException {
        String dst_id = "DST1";
        String item_code = "ITEM1";
        int item_num = 10;

        PrepaymentDto prepaymentDto = new PrepaymentDto(false, "CERT123");
        when(msgManager.prepaymentRequest(anyString(), eq(dst_id), eq(item_code), eq(item_num))).thenReturn(prepaymentDto);
        when(sortedDVM.getNearestDVM(item_code)).thenReturn(null);

        machine.ProcessPrepayment(dst_id, item_code, item_num);

        verify(printManager, times(1)).displayNextDVM(null, 0, 0);
        verify(sortedDVM, never()).removeRemainSortedDVM(item_code);
    }

    @Test
    void testInsertCardData() {
        String card_id = "CARD123";
        String item_code = "ITEM1";
        int item_num = 10;
        boolean isPrepay = true;

        machine.insertCardData(card_id, item_code, item_num, isPrepay);

        verify(saleManager, times(1)).checkCardData(card_id, item_code, item_num, isPrepay);
    }

    @Test
    void testStockResponse() {
        JsonObject message = new JsonObject();
        message.addProperty("src_id", "SRC1");
        message.addProperty("dst_id", "DST1");
        JsonObject msg_content = new JsonObject();
        msg_content.addProperty("item_code", "ITEM1");
        msg_content.addProperty("item_num", 10);
        message.add("msg_content", msg_content);

        machine.stockResponse(message);

        verify(msgManager, times(1)).stockResponse(eq("DST1"), eq("SRC1"), any(int[].class), eq("ITEM1"), eq(10));
    }

    @Test
    void testPrepaymentResponse() {
        JsonObject message = new JsonObject();
        message.addProperty("src_id", "SRC1");
        message.addProperty("dst_id", "DST1");
        JsonObject msg_content = new JsonObject();
        msg_content.addProperty("item_code", "ITEM1");
        msg_content.addProperty("item_num", 10);
        msg_content.addProperty("cert_code", "CERT123");
        message.add("msg_content", msg_content);

        machine.prepaymentResponse(message);

        verify(msgManager, times(1)).prepaymentResponse(eq("DST1"), eq("SRC1"), eq("ITEM1"), eq(10), eq("CERT123"));
    }

    @Test
    void testRefundPrepayment() {
        String card_data = "CARD123";
        String item_code = "ITEM1";
        int item_num = 10;

        machine.refundPrepayment(card_data, item_code, item_num);

        verify(saleManager, times(1)).refundPrepayment(card_data, item_code, item_num);
        verify(sortedDVM, times(1)).removeRemainSortedDVM(item_code);
        verify(printManager, times(1)).displayRefund();
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
