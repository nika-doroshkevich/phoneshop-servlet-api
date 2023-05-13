package com.es.phoneshop.model.cart;

import com.es.phoneshop.exception.OutOfStockException;

public interface CartService {

    Cart getCart();

    void add(Long productId, int quantity) throws OutOfStockException;

}
