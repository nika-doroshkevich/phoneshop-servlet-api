package com.es.phoneshop.model.product;

import com.es.phoneshop.exception.BadRequestException;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.NoSuchElementException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class ArrayListProductDaoTest
{
    private ProductDao productDao;

    @Before
    public void setup() {
        productDao = new ArrayListProductDao();
    }

    @Test(expected = BadRequestException.class)
    public void testGetProductIdNull() {
        productDao.getProduct(null);
    }

    @Test
    public void testGetProductSuccessfully() {
        var product = productDao.getProduct(1L);
        Long expectedId = 1L;
        assertNotNull(product);
        assertEquals(expectedId, product.getId());
    }

    @Test(expected = NoSuchElementException.class)
    public void testGetProductNotInList() {
        productDao.getProduct(-1L);
    }

    @Test
    public void testFindProductsThereIsResults() {
        var products = productDao.findProducts();
        assertFalse(products.isEmpty());
        assertEquals(12, products.size());
    }

    @Test
    public void testSaveProductIdNotNull() {
        Currency usd = Currency.getInstance("USD");
        var product = new Product("test", "Test", new BigDecimal(100), usd, 100, "test");
        product.setId(1L);
        Long expectedId = 1L;
        productDao.save(product);
        var expectedProduct = productDao.getProduct(1L);
        assertEquals(expectedId, expectedProduct.getId());
        assertEquals("test", expectedProduct.getCode());
    }

    @Test(expected = NoSuchElementException.class)
    public void testSaveProductIdNotNullAndObjectNull() {
        Currency usd = Currency.getInstance("USD");
        var product = new Product("test", "Test", new BigDecimal(100), usd, 100, "test");
        product.setId(-1L);
        productDao.save(product);
    }

    @Test
    public void testSaveProductIdNull() {
        var sizeBefore = productDao.findProducts().size();
        Currency usd = Currency.getInstance("USD");
        var product = new Product("test", "Test", new BigDecimal(100), usd, 100, "test");
        productDao.save(product);
        var sizeAfter = productDao.findProducts().size();
        assertEquals(sizeBefore + 1, sizeAfter);
    }

    @Test
    public void testDeleteSuccessfully() {
        var sizeBefore = productDao.findAllProducts().size();
        productDao.delete(1L);
        var sizeAfter = productDao.findAllProducts().size();
        assertEquals(sizeBefore - 1, sizeAfter);
    }

    @Test(expected = NoSuchElementException.class)
    public void testDeleteThereIsNoIdInList() {
        productDao.delete(-1L);
    }
}
