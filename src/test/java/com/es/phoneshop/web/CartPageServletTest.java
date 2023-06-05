package com.es.phoneshop.web;

import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartService;
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
import java.util.Locale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CartPageServletTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private ServletConfig config;
    @Mock
    private CartService cartService;
    @Mock
    private RequestDispatcher requestDispatcher;

    private CartPageServlet servlet = new CartPageServlet();

    @Before
    public void setup() throws ServletException, IllegalAccessException {
        servlet.init(config);
        FieldUtils.writeField(servlet, "cartService", cartService, true);
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
    }

    @Test
    public void testDoGet() throws ServletException, IOException {
        var cart = new Cart();
        when(cartService.getCart(eq(request))).thenReturn(cart);
        servlet.doGet(request, response);
        verify(cartService).getCart(any());
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoPost() throws ServletException, IOException, OutOfStockException {
        String[] arr1 = {"1"};
        when(request.getParameterValues(eq("productId"))).thenReturn(arr1);
        String[] arr2 = {"1"};
        when(request.getParameterValues(eq("quantity"))).thenReturn(arr2);
        Locale locale = new Locale("");
        when(request.getLocale()).thenReturn(locale);
        var cart = new Cart();
        when(cartService.getCart(eq(request))).thenReturn(cart);

        servlet.doPost(request, response);

        verify(cartService).getCart(any());
        verify(cartService).update(any(), any(), anyInt());
        verify(response).sendRedirect(anyString());
    }

    @Test
    public void testDoPostWithError() throws ServletException, IOException, OutOfStockException {
        String[] arr1 = {"1"};
        when(request.getParameterValues(eq("productId"))).thenReturn(arr1);
        String[] arr2 = {"a"};
        when(request.getParameterValues(eq("quantity"))).thenReturn(arr2);
        Locale locale = new Locale("");
        when(request.getLocale()).thenReturn(locale);
        var cart = new Cart();
        when(cartService.getCart(eq(request))).thenReturn(cart);

        servlet.doPost(request, response);

        verify(cartService).getCart(any());
        verify(cartService, times(0)).update(any(), any(), anyInt());
        verify(response, times(0)).sendRedirect(anyString());
    }
}
