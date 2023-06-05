package com.es.phoneshop.model.cart;

import com.es.phoneshop.model.product.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CartItem {

    private Product product;
    private int quantity;

    @Override
    public String toString() {
        return product.getCode() + ", " + quantity;
    }
}
