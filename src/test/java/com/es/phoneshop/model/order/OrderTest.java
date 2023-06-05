package com.es.phoneshop.model.order;

import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

public class OrderTest {

    @Test
    public void testGetter() {
        Order order = new Order();
        Long id = 1L;
        order.setId(id);
        order.setSecureId("2");
        order.setSubtotal(new BigDecimal(3));
        order.setDeliveryCost(new BigDecimal(4));
        order.setFirstName("5");
        order.setLastName("6");
        order.setPhone("7");
        var now = LocalDate.now();
        order.setDeliveryDate(now);
        order.setDeliveryAddress("8");
        order.setPaymentMethod(PaymentMethod.CASH);

        assertEquals(id, order.getId());
        assertEquals("2", order.getSecureId());
        assertEquals(new BigDecimal(3), order.getSubtotal());
        assertEquals(new BigDecimal(4), order.getDeliveryCost());
        assertEquals("5", order.getFirstName());
        assertEquals("6", order.getLastName());
        assertEquals("7", order.getPhone());
        assertEquals(now, order.getDeliveryDate());
        assertEquals("8", order.getDeliveryAddress());
        assertEquals(PaymentMethod.CASH, order.getPaymentMethod());
    }
}
