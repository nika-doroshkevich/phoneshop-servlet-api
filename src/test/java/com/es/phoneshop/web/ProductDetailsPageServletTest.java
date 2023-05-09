package com.es.phoneshop.web;

import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.model.product.price.ProductPricesDto;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Currency;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProductDetailsPageServletTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private ServletConfig config;
    @Mock
    private ProductDao productDao;

    private ProductDetailsPageServlet servlet = new ProductDetailsPageServlet();

    @Before
    public void setup() throws ServletException, IllegalAccessException {
        servlet.init(config);
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        Currency usd = Currency.getInstance("USD");
        var product = new Product("test", "Test", new BigDecimal(100), usd, 100, "test");
        productDao.save(product);
        FieldUtils.writeField(servlet, "productDao", productDao, true);
    }

    @Test
    public void testDoGetProductPrices() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/prices/1");
        ProductPricesDto productPricesDto = new ProductPricesDto();
        when(productDao.getProductPrices(1L)).thenReturn(productPricesDto);
        servlet.doGet(request, response);
        verify(requestDispatcher).forward(request, response);
        verify(request).setAttribute(eq("productPricesDto"), any());
    }

    @Test
    public void testDoGetProductDetails() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/1");
        Currency usd = Currency.getInstance("USD");
        var product = new Product("test", "Test", new BigDecimal(100), usd, 100, "test");
        when(productDao.getProduct(1L)).thenReturn(product);
        servlet.doGet(request, response);
        verify(requestDispatcher).forward(request, response);
        verify(request).setAttribute(eq("product"), any());
    }
}
