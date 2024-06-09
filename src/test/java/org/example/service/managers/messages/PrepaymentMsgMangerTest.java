package org.example.service.managers.messages;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.example.service.managers.SaleManager;
import org.example.service.managers.StockManager;
import org.example.service.socket.JsonSocketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PrepaymentMsgMangerTest {
    @Mock
    private JsonSocketService jsonSocketService;

    @Mock
    private StockManager stockManager;

    @Mock
    private SaleManager saleManager;

    @InjectMocks
    private PrepaymentMsgManager prepaymentMsgManager;

    private Gson gson = new Gson();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRequest() {
        String id = "id";
        String dst_id = "dst_id";
        String selected_code = "01";
        int selected_num = 10;
        String cert_code = "cert_code";

        JsonObject response = new JsonObject();
        response.addProperty("msg_type", "resp_prepay");
        JsonObject msg_content = new JsonObject();
        msg_content.addProperty("item_code", selected_code);
        msg_content.addProperty("item_num", selected_num);
        msg_content.addProperty("availability", true);
        response.add("msg_content", msg_content);

        JsonSocketService jsonRequestSocketServiceMock = mock(JsonSocketService.class);
        when(jsonRequestSocketServiceMock.receiveMessage()).thenReturn(response.toString());

        boolean result = prepaymentMsgManager.request(id, dst_id, selected_code, selected_num, cert_code, jsonRequestSocketServiceMock);
        assertTrue(result);

        // Verify that sendMessage was called and capture the argument passed to it
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(jsonRequestSocketServiceMock).sendMessage(captor.capture());

        String sentMessage = captor.getValue();
        JsonObject sentJson = gson.fromJson(sentMessage, JsonObject.class);
        assertEquals("req_prepay", sentJson.get("msg_type").getAsString());
        assertEquals(id, sentJson.get("src_id").getAsString());
        assertEquals(dst_id, sentJson.get("dst_id").getAsString());
        JsonObject msgContent = sentJson.get("msg_content").getAsJsonObject();
        assertEquals(selected_code, msgContent.get("item_code").getAsString());
        assertEquals(selected_num, msgContent.get("item_num").getAsInt());
        assertEquals(cert_code, msgContent.get("cert_code").getAsString());
    }

    @Test
    void testResponse() {
        String id = "id";
        String dst_id = "dst_id";
        String item_code = "01";
        int item_num = 10;
        String cert_code = "cert_code";

        when(stockManager.checkStock(item_code, item_num)).thenReturn(item_num);

        prepaymentMsgManager.response(id, dst_id, item_code, item_num, cert_code);
        verify(jsonSocketService, times(1)).sendMessage(anyString());
        verify(saleManager, times(1)).processPrepayment(item_code, item_num, cert_code);

        // Optionally, capture the argument and validate the sent message
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(jsonSocketService).sendMessage(captor.capture());

        String sentMessage = captor.getValue();
        JsonObject sentJson = gson.fromJson(sentMessage, JsonObject.class);
        assertEquals("resp_prepay", sentJson.get("msg_type").getAsString());
        assertEquals(id, sentJson.get("src_id").getAsString());
        assertEquals(dst_id, sentJson.get("dst_id").getAsString());
        JsonObject msgContent = sentJson.get("msg_content").getAsJsonObject();
        assertEquals(item_code, msgContent.get("item_code").getAsString());
        assertEquals(item_num, msgContent.get("item_num").getAsInt());
        assertTrue(msgContent.get("availability").getAsBoolean());
    }

    @Test
    void testResponseWithInsufficientStock() {
        String id = "id";
        String dst_id = "dst_id";
        String item_code = "01";
        int item_num = 10;
        String cert_code = "cert_code";

        when(stockManager.checkStock(item_code, item_num)).thenReturn(5);

        prepaymentMsgManager.response(id, dst_id, item_code, item_num, cert_code);
        verify(jsonSocketService, times(1)).sendMessage(anyString());
        verify(saleManager, times(0)).processPrepayment(anyString(), anyInt(), anyString());

        // Optionally, capture the argument and validate the sent message
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(jsonSocketService).sendMessage(captor.capture());

        String sentMessage = captor.getValue();
        JsonObject sentJson = gson.fromJson(sentMessage, JsonObject.class);
        assertEquals("resp_prepay", sentJson.get("msg_type").getAsString());
        assertEquals(id, sentJson.get("src_id").getAsString());
        assertEquals(dst_id, sentJson.get("dst_id").getAsString());
        JsonObject msgContent = sentJson.get("msg_content").getAsJsonObject();
        assertEquals(item_code, msgContent.get("item_code").getAsString());
        assertEquals(10, msgContent.get("item_num").getAsInt());
        assertFalse(msgContent.get("availability").getAsBoolean());
    }
}
