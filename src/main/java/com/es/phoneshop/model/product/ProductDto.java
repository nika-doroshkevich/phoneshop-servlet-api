package com.es.phoneshop.model.product;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDto {
    private Product product;
    private int numberOfMatches;

    public ProductDto(Product product) {
        this.product = product;
        this.numberOfMatches = 0;
    }
}
