package org.example.service.managers;

import org.example.model.Card;
import org.example.model.PrepaymentState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SaleManagerTest {

    @Mock
    private StockManager stockManager;

    @Mock
    private PrintManager printManager;

    @Mock
    private PrepaymentState prepaymentState;

    @Mock
    private Card card;

    @InjectMocks
    private SaleManager saleManager;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        saleManager = new SaleManager(stockManager, printManager, prepaymentState, card);
    }

    @Test
    public void testProcessPrepayment() {
        String item_code = "01";
        int item_num = 2;
        String cert_code = "cert123";

        saleManager.processPrepayment(item_code, item_num, cert_code);

        verify(stockManager, times(1)).saleStock(item_code, item_num);
        verify(prepaymentState, times(1)).storePrePayment(item_code, item_num, cert_code);
    }

    @Test
    public void testOfferPrepaidItemValid() {
        String cert_code = "cert123";
        Map<String, String> map = new HashMap<>();
        map.put("item_code", "01");
        map.put("item_num", "2");

        when(prepaymentState.checkCode(cert_code)).thenReturn(map);

        saleManager.offerPrepaidItem(cert_code);

        verify(printManager, times(1)).displayValidCode("01", 2);
    }

    @Test
    public void testOfferPrepaidItemInvalid() {
        String cert_code = "cert123";
        Map<String, String> map = new HashMap<>();
        map.put("item_code", "0");

        when(prepaymentState.checkCode(cert_code)).thenReturn(map);

        saleManager.offerPrepaidItem(cert_code);

        verify(printManager, times(1)).displayInvalidCode();
    }

    @Test
    public void testOfferItemWithSufficientStock() {
        String selected_code = "01";
        int selected_num = 2;

        when(stockManager.checkStock(selected_code, selected_num)).thenReturn(3);

        boolean result = saleManager.offerItem(selected_code, selected_num);

        assertTrue(result);
        verify(printManager, times(1)).offerItem();
    }

    @Test
    public void testOfferItemWithInsufficientStock() {
        String selected_code = "01";
        int selected_num = 2;

        when(stockManager.checkStock(selected_code, selected_num)).thenReturn(1);

        boolean result = saleManager.offerItem(selected_code, selected_num);

        assertFalse(result);
        verify(printManager, never()).offerItem();
    }

    @Test
    public void testCheckCardDataWithValidCard() {
        String card_id = "card1";
        String item_code = "01";
        int item_num = 2;
        boolean isPrepay = false;

        when(stockManager.checkPrice(item_code)).thenReturn(100);
        when(card.checkCardData(card_id, 200)).thenReturn(true);

        saleManager.checkCardData(card_id, item_code, item_num, isPrepay);

        verify(stockManager, times(1)).saleStock(item_code, item_num);
        verify(printManager, times(1)).displayPayment(true);
    }

    @Test
    public void testCheckCardDataWithInvalidCard() {
        String card_id = "card1";
        String item_code = "01";
        int item_num = 2;
        boolean isPrepay = false;

        when(stockManager.checkPrice(item_code)).thenReturn(100);
        when(card.checkCardData(card_id, 200)).thenReturn(false);

        saleManager.checkCardData(card_id, item_code, item_num, isPrepay);

        verify(stockManager, never()).saleStock(anyString(), anyInt());
        verify(printManager, times(1)).displayPayment(false);
    }

    @Test
    public void testRefundPrepayment() {
        String card_data = "card1";
        String item_code = "01";
        int item_num = 2;

        when(stockManager.checkPrice(item_code)).thenReturn(100);

        saleManager.refundPrepayment(card_data, item_code, item_num);

        verify(card, times(1)).refundCardData(card_data, 200);
    }
}
