package com.es.phoneshop.model.cart;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

@Getter
@Setter
public class Cart implements Serializable {

    private List<CartItem> items;

    private int totalQuantity;
    private TotalCost totalCost;

    public Cart() {
        this.items = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Cart{" +
                "items=" + items +
                '}';
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public class TotalCost {
        BigDecimal totalCost;
        Currency currency;
    }
}
