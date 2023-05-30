package com.es.phoneshop.model.order;

public interface OrderDao {
    Order getOrder(Long id);

    Order getOrderBySecureId(String secureId);

    void save(Order order);
}
