package org.example.service.managers.messages;

import org.example.model.DVM;
import org.example.model.SortedDVM;
import org.example.model.dto.ClosestDVMDto;
import org.example.model.dto.DVMDto;
import org.example.model.dto.PrepaymentDto;
import org.example.service.managers.SaleManager;
import org.example.service.managers.StockManager;
import org.example.service.socket.JsonSocketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class MsgManagerTest {
    @Mock
    private DVM dvm;

    @Mock
    private SortedDVM sortedDVM;

    @Mock
    private PrepaymentMsgManager prepaymentMsgManager;

    @Mock
    private StockMsgManager stockMsgManager;

    @InjectMocks
    private MsgManager msgManager;

    private final int[] coordinate = {0, 0};

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        MockitoAnnotations.openMocks(this);

        // Initialize the coordinate field
        Field coordinateField = MsgManager.class.getDeclaredField("coordinate");
        coordinateField.setAccessible(true);
        coordinateField.set(msgManager, coordinate);

        // Use reflection to set private fields
        Field prepaymentField = MsgManager.class.getDeclaredField("prepaymentMsgManager");
        prepaymentField.setAccessible(true);
        prepaymentField.set(msgManager, prepaymentMsgManager);

        Field stockField = MsgManager.class.getDeclaredField("stockMsgManager");
        stockField.setAccessible(true);
        stockField.set(msgManager, stockMsgManager);
    }

    @Test
    void testMakeCode() {
        String code = msgManager.makeCode();
        assertNotNull(code);
        assertEquals(10, code.length());
    }

    @Test
    void testStockResponse() {
        msgManager.stockResponse("dst_id", "src_id", coordinate, "01", 10);
        verify(stockMsgManager, times(1)).response(anyString(), anyString(), any(), anyString(), anyInt());
    }

    @Test
    void testPrepaymentResponse() {
        msgManager.prepaymentResponse("dst_id", "src_id", "01", 10, "cert_code");
        verify(prepaymentMsgManager, times(1)).response(anyString(), anyString(), anyString(), anyInt(), anyString());
    }

    @Test
    void testStockRequest() throws Exception {
        List<DVMDto> dvmList = Arrays.asList(
                new DVMDto("1", "127.0.0.1", "8080"),
                new DVMDto("2", "127.0.0.1", "8081")
        );
        when(dvm.getAllDVM()).thenReturn(dvmList);
        when(sortedDVM.getNearestDVM(anyString())).thenReturn(new ClosestDVMDto("1", 1,1));
        MsgManager spyMsgManager = spy(msgManager);

        for (DVMDto dvmDto : dvmList) {
            JsonSocketService jsonSocketServiceMock = mock(JsonSocketService.class);
            doReturn(jsonSocketServiceMock).when(spyMsgManager).connectSocket(dvmDto.getIp(), Integer.parseInt(dvmDto.getPort()));
            spyMsgManager.connectSocket(dvmDto.getIp(), Integer.parseInt(dvmDto.getPort()));
        }
        ClosestDVMDto result = spyMsgManager.stockRequest("id", "item_code", 10);
        assertNotNull(result);
    }
    @Test
    void testPrepaymentRequest() throws Exception {
        DVMDto dvmDto = new DVMDto("dst_id", "127.0.0.1", "8080");
        when(dvm.getIpPort(anyString())).thenReturn(dvmDto);

        JsonSocketService jsonSocketServiceMock = mock(JsonSocketService.class);
        MsgManager spyMsgManager = spy(msgManager);
        doReturn(jsonSocketServiceMock).when(spyMsgManager).connectSocket(dvmDto.getIp(), Integer.parseInt(dvmDto.getPort()));

        PrepaymentDto result = spyMsgManager.prepaymentRequest("id", "dst_id", "01", 10);
        assertNotNull(result);
    }
}
