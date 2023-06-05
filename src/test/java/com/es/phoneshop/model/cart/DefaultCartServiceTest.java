package com.es.phoneshop.model.cart;

import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultCartServiceTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpSession httpSession;

    private ProductDao productDao;

    private DefaultCartService defaultCartService = DefaultCartService.getInstance();

    @Before
    public void setup() {
        productDao = ArrayListProductDao.getInstance();

        var usd = Currency.getInstance("USD");
        productDao.save(new Product("test1", "Samsung1", new BigDecimal(100), usd, 100, "test"));
        productDao.save(new Product("test2", "Samsung Test", new BigDecimal(200), usd, 100, "test"));
        productDao.save(new Product("test3", "Samsung Test S", new BigDecimal(300), usd, 100, "test"));
    }

    @Test
    public void testGetCart() {
        var cart = new Cart();
        cart.setTotalQuantity(1);
        when(request.getSession()).thenReturn(httpSession);
        when(httpSession.getAttribute("com.es.phoneshop.model.cart.DefaultCartService.cart")).thenReturn(cart);

        var foundCart = defaultCartService.getCart(request);

        assertEquals(1, foundCart.getTotalQuantity());
    }

    @Test
    public void testAdd() throws OutOfStockException {
        var cart = new Cart();
        cart.setTotalQuantity(1);
        defaultCartService.add(cart, 1L, 1);
        assertNotNull(cart);
        defaultCartService.add(cart, 1L, 1);
        assertNotNull(cart);
    }
}
