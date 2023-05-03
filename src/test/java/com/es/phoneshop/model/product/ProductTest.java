package com.es.phoneshop.model.product;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ProductTest {

    @Test
    public void testIsAvailableForSaleTrue() {
        var product = new Product();
        product.setPrice(new BigDecimal(100));
        product.setStock(5);
        assertTrue(product.isAvailableForSale());
    }

    @Test
    public void testIsAvailableForSaleFalse() {
        var product1 = new Product();
        assertFalse(product1.isAvailableForSale());

        var product2 = new Product();
        product2.setPrice(new BigDecimal(100));
        assertFalse(product2.isAvailableForSale());

        var product3 = new Product();
        product3.setStock(5);
        assertFalse(product3.isAvailableForSale());
    }
}
