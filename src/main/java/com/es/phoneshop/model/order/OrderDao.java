package com.es.phoneshop.model.order;

import com.es.phoneshop.model.BaseDao;

public interface OrderDao extends BaseDao<Order> {

    Order getOrderBySecureId(String secureId);
}
