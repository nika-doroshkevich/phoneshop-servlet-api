package com.es.phoneshop.model.product.price;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductPrice {

    private Long productId;
    private BigDecimal price;
    private LocalDate date;
    private Currency currency;
}
