package com.es.phoneshop.exception;

import com.es.phoneshop.model.product.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OutOfStockException extends Exception {
    private Product product;
    private int stockRequested;
    private int stockAvailable;
}
