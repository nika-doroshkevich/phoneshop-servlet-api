package com.es.phoneshop.model.order;

import com.es.phoneshop.model.cart.Cart;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static com.es.phoneshop.model.order.PaymentMethod.CASH;
import static com.es.phoneshop.model.order.PaymentMethod.CREDIT_CARD;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DefaultOrderServiceTest {

    private OrderDao orderDao;
    private OrderService orderService;

    @Before
    public void setup() {
        orderDao = ArrayListOrderDao.getInstance();
        orderService = DefaultOrderService.getInstance();
    }

    @Test
    public void testGetOrderSuccessfully() {
        var cart1 = new Cart();
        cart1.setTotalQuantity(1);
        cart1.setTotalCost(new BigDecimal(100));
        var order = orderService.getOrder(cart1);

        assertEquals(1, order.getTotalQuantity());
    }

    @Test
    public void testGetPaymentMethods() {
        var listOfPaymentMethods = orderService.getPaymentMethods();

        assertEquals(CASH, listOfPaymentMethods.get(0));
        assertEquals(CREDIT_CARD, listOfPaymentMethods.get(1));
    }

    @Test
    public void testPlaceOrderSecureIdNotNull() {
        var order1 = new Order();
        orderDao.save(order1);
        orderService.placeOrder(order1);
        var secureId = order1.getSecureId();

        assertNotNull(secureId);
    }
}
