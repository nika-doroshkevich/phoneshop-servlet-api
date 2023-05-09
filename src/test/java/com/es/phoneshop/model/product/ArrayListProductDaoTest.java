package com.es.phoneshop.model.product;

import com.es.phoneshop.exception.BadRequestException;
import com.es.phoneshop.model.product.price.ProductPrice;
import com.es.phoneshop.model.product.price.ProductPricesDto;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.NoSuchElementException;

import static com.es.phoneshop.model.product.SortField.description;
import static com.es.phoneshop.model.product.SortOrder.asc;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class ArrayListProductDaoTest {

    private ProductDao productDao;

    @Before
    public void setup() {
        productDao = ArrayListProductDao.getInstance();

        var usd = Currency.getInstance("USD");
        productDao.save(new Product("test1", "Samsung1", new BigDecimal(100), usd, 100, "test"));
        productDao.save(new Product("test2", "Samsung Test", new BigDecimal(200), usd, 1, "test"));
        productDao.save(new Product("test3", "Samsung Test S", new BigDecimal(300), usd, 5, "test"));
    }

    @After
    public void after() throws IllegalAccessException {
        List<Product> emptyListProducts = new ArrayList<>();
        FieldUtils.writeField(productDao, "products", emptyListProducts, true);
        FieldUtils.writeField(productDao, "maxId", 0L, true);
        List<ProductPrice> emptyListPriceHistory = new ArrayList<>();
        FieldUtils.writeField(productDao, "priceHistory", emptyListPriceHistory, true);
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
        var products = productDao.findProducts("", description, asc);
        assertFalse(products.isEmpty());
        assertEquals(3, products.size());
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
        var sizeBefore = productDao.findProducts("", description, asc).size();
        Currency usd = Currency.getInstance("USD");
        var product = new Product("test", "Test", new BigDecimal(100), usd, 100, "test");
        productDao.save(product);
        var sizeAfter = productDao.findProducts("", description, asc).size();
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

    @Test
    public void testFindProductsQueryNullOrEmpty() {
        var expectedSize = productDao.findProducts().size();
        var productsQueryNull = productDao.findProducts(null, description, asc);
        assertEquals(expectedSize, productsQueryNull.size());
        var productsQueryEmpty = productDao.findProducts("", description, asc);
        assertEquals(expectedSize, productsQueryEmpty.size());
    }

    @Test
    public void testFindProductsQuerySuccessfully() {
        var products = productDao.findProducts("samsung S", description, asc);
        assertEquals(3, products.size());
    }

    @Test
    public void testFindProductsQueryNonMatch() {
        var products = productDao.findProducts("b d", description, asc);
        assertEquals(0, products.size());
    }

    @Test
    public void testGetProductPrices() {
        ProductPricesDto productPricesDto = productDao.getProductPrices(1L);
        assertEquals("Samsung Test", productPricesDto.getProductDescription());
        List<ProductPrice> prices = productPricesDto.getPrices();
        assertEquals(1, prices.size());
        ProductPrice productPrice = prices.get(0);
        Long productId = 1L;
        assertEquals(productId, productPrice.getProductId());
        BigDecimal price = new BigDecimal(200);
        assertEquals(price, productPrice.getPrice());
        assertEquals(LocalDate.now(), productPrice.getDate());
        var currency = Currency.getInstance("USD");
        assertEquals(currency, productPrice.getCurrency());
    }
}
