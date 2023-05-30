package com.es.phoneshop.model.order;

public interface OrderDao {
    Order getOrder(Long id);

    void save(Order order);
}
