package com.es.phoneshop.model.product;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.LinkedList;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RecentlyViewedProductsServiceTest {

    @Mock
    private HttpServletRequest httpServletRequest;
    @Mock
    private HttpSession httpSession;

    @Test
    public void testAddProductToRecentlyViewedProductsFromSessionNull() {
        RecentlyViewedProductsServiceImpl recentlyViewedProductsService = new RecentlyViewedProductsServiceImpl();
        Product product = new Product();
        when(httpServletRequest.getSession()).thenReturn(httpSession);
        recentlyViewedProductsService.addProductToRecentlyViewed(httpServletRequest, product);
        var products = new LinkedList<>();
        products.addFirst(product);
        verify(httpSession).setAttribute(eq("com.es.phoneshop.model.product.ArrayListProductDao.recentlyViewedProducts"), eq(products));
    }

    @Test
    public void testAddProductToRecentlyViewedProductsFromSessionNotNull() {
        RecentlyViewedProductsServiceImpl recentlyViewedProductsService = new RecentlyViewedProductsServiceImpl();
        Product product = new Product();
        var products = new LinkedList<>();
        products.addFirst(product);
        LinkedList<Product> productsFromSession = new LinkedList<>();
        when(httpServletRequest.getSession()).thenReturn(httpSession);
        when(httpSession.getAttribute(eq("com.es.phoneshop.model.product.ArrayListProductDao.recentlyViewedProducts"))).thenReturn(productsFromSession);
        recentlyViewedProductsService.addProductToRecentlyViewed(httpServletRequest, product);
        verify(httpSession).setAttribute(eq("com.es.phoneshop.model.product.ArrayListProductDao.recentlyViewedProducts"), eq(products));
    }
}
