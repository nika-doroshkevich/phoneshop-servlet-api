package com.es.phoneshop.model.cart;

public interface CartService {

    Cart getCart();

    void add(Long productId, int quantity);

}
