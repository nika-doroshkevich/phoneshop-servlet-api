package com.es.phoneshop.model.cart;

import com.es.phoneshop.model.product.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
public class CartItem implements Serializable {

    private Product product;
    private int quantity;

    @Override
    public String toString() {
        return product.getCode() + ", " + quantity;
    }
}
