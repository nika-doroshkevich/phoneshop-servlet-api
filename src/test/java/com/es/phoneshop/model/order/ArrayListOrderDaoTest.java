package com.es.phoneshop.model.order;

import com.es.phoneshop.exception.BadRequestException;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class ArrayListOrderDaoTest {

    private OrderDao orderDao;

    @Before
    public void setup() {
        orderDao = ArrayListOrderDao.getInstance();

        var order1 = new Order();
        order1.setSecureId("1");
        orderDao.save(order1);
        var order2 = new Order();
        order2.setSecureId("2");
        orderDao.save(order2);
        var order3 = new Order();
        order3.setSecureId("3");
        orderDao.save(order3);
    }

    @After
    public void after() throws IllegalAccessException {
        List<Order> emptyListOrders = new ArrayList<>();
        FieldUtils.writeField(orderDao, "orders", emptyListOrders, true);
        FieldUtils.writeField(orderDao, "maxId", 0L, true);
    }

    @Test
    public void testGetInstance() {
        var instance1 = ArrayListOrderDao.getInstance();
        assertNotNull(instance1);
        var instance2 = ArrayListOrderDao.getInstance();
        assertEquals(instance1, instance2);
    }

    @Test(expected = BadRequestException.class)
    public void testGetEntityNull() {
        Long orderId = null;
        orderDao.getEntity(orderId);
    }

    @Test
    public void testGetEntity() {
        var order = orderDao.getEntity(1L);
        Long expectedId = 1L;
        assertNotNull(order);
        assertEquals(expectedId, order.getId());
    }

    @Test(expected = BadRequestException.class)
    public void testGetOrderBySecureIdNull() {
        String secureId = null;
        var order = orderDao.getOrderBySecureId(secureId);
    }

    @Test
    public void testGetOrderBySecureId() {
        String secureId = "1";
        var order = orderDao.getOrderBySecureId(secureId);
        assertNotNull(order);
        assertEquals(secureId, order.getSecureId());
    }

    @Test
    public void testSave() {
        var order = new Order();
        order.setId(1L);
        order.setSecureId("999");
        orderDao.save(order);

        var foundOrder = orderDao.getEntity(1L);
        assertEquals("999", foundOrder.getSecureId());
    }
}
