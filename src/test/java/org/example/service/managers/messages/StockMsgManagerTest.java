package org.example.service.managers.messages;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.example.model.dto.CoorDto;
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

class StockMsgManagerTest {

    @Mock
    private JsonSocketService jsonSocketService;

    @Mock
    private StockManager stockManager;

    @InjectMocks
    private StockMsgManager stockMsgManager;

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

        JsonObject response = new JsonObject();
        response.addProperty("msg_type", "resp_stock");
        JsonObject msg_content = new JsonObject();
        msg_content.addProperty("item_code", selected_code);
        msg_content.addProperty("item_num", selected_num);
        msg_content.addProperty("coor_x", 100);
        msg_content.addProperty("coor_y", 200);
        response.add("msg_content", msg_content);

        JsonSocketService jsonRequestSocketServiceMock = mock(JsonSocketService.class);
        when(jsonRequestSocketServiceMock.receiveMessage()).thenReturn(response.toString());

        CoorDto result = stockMsgManager.request(id, dst_id, selected_code, selected_num, jsonRequestSocketServiceMock);
        assertNotNull(result);
        assertEquals(dst_id, result.getDstId());
        assertEquals(100, result.getCoorX());
        assertEquals(200, result.getCoorY());
        assertEquals(selected_code, result.getItemCode());

        // Verify that sendMessage was called and capture the argument passed to it
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(jsonRequestSocketServiceMock).sendMessage(captor.capture());

        String sentMessage = captor.getValue();
        JsonObject sentJson = gson.fromJson(sentMessage, JsonObject.class);
        assertEquals("req_stock", sentJson.get("msg_type").getAsString());
        assertEquals(id, sentJson.get("src_id").getAsString());
        assertEquals(dst_id, sentJson.get("dst_id").getAsString());
        JsonObject msgContent = sentJson.get("msg_content").getAsJsonObject();
        assertEquals(selected_code, msgContent.get("item_code").getAsString());
        assertEquals(selected_num, msgContent.get("item_num").getAsInt());
    }

    @Test
    void testResponse() {
        String id = "id";
        String dst_id = "dst_id";
        String item_code = "01";
        int item_num = 10;
        int[] coor = {100, 200};

        when(stockManager.checkStock(item_code, item_num)).thenReturn(item_num);

        stockMsgManager.response(id, dst_id, coor, item_code, item_num);
        verify(jsonSocketService, times(1)).sendMessage(anyString());

        // Optionally, capture the argument and validate the sent message
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(jsonSocketService).sendMessage(captor.capture());

        String sentMessage = captor.getValue();
        JsonObject sentJson = gson.fromJson(sentMessage, JsonObject.class);
        assertEquals("resp_stock", sentJson.get("msg_type").getAsString());
        assertEquals(id, sentJson.get("src_id").getAsString());
        assertEquals(dst_id, sentJson.get("dst_id").getAsString());
        JsonObject msgContent = sentJson.get("msg_content").getAsJsonObject();
        assertEquals(item_code, msgContent.get("item_code").getAsString());
        assertEquals(item_num, msgContent.get("item_num").getAsInt());
        assertEquals(coor[0], msgContent.get("coor_x").getAsInt());
        assertEquals(coor[1], msgContent.get("coor_y").getAsInt());
    }

    @Test
    void testResponseWithInsufficientStock() {
        String id = "id";
        String dst_id = "dst_id";
        String item_code = "01";
        int item_num = 10;
        int[] coor = {100, 200};

        when(stockManager.checkStock(item_code, item_num)).thenReturn(5);

        stockMsgManager.response(id, dst_id, coor, item_code, item_num);
        verify(jsonSocketService, times(1)).sendMessage(anyString());

        // Optionally, capture the argument and validate the sent message
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(jsonSocketService).sendMessage(captor.capture());

        String sentMessage = captor.getValue();
        JsonObject sentJson = gson.fromJson(sentMessage, JsonObject.class);
        assertEquals("resp_stock", sentJson.get("msg_type").getAsString());
        assertEquals(id, sentJson.get("src_id").getAsString());
        assertEquals(dst_id, sentJson.get("dst_id").getAsString());
        JsonObject msgContent = sentJson.get("msg_content").getAsJsonObject();
        assertEquals(item_code, msgContent.get("item_code").getAsString());
        assertEquals(5, msgContent.get("item_num").getAsInt());
        assertEquals(coor[0], msgContent.get("coor_x").getAsInt());
        assertEquals(coor[1], msgContent.get("coor_y").getAsInt());
    }
}
