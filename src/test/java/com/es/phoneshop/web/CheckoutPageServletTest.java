package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.DefaultCartService;
import com.es.phoneshop.model.order.DefaultOrderService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CheckoutPageServletTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private ServletConfig config;
    @Mock
    private HttpSession httpSession;

    private CheckoutPageServlet servlet = new CheckoutPageServlet();
    private DefaultCartService defaultCartService = DefaultCartService.getInstance();
    private DefaultOrderService defaultOrderService = DefaultOrderService.getInstance();

    @Before
    public void setup() throws ServletException, IllegalAccessException {
        servlet.init(config);
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        FieldUtils.writeField(servlet, "cartService", defaultCartService, true);
        FieldUtils.writeField(servlet, "orderService", defaultOrderService, true);
        when(request.getSession()).thenReturn(httpSession);
    }

    @Test
    public void testDoGet() throws ServletException, IOException {
        var cart = new Cart();
        cart.setTotalCost(new BigDecimal(1));
        when(httpSession.getAttribute(eq("com.es.phoneshop.model.cart.DefaultCartService.cart"))).thenReturn(cart);

        servlet.doGet(request, response);

        verify(requestDispatcher).forward(request, response);
        verify(request).setAttribute(eq("order"), any());
        verify(request).setAttribute(eq("paymentMethods"), any());
    }

    @Test
    public void testDoPost() throws ServletException, IOException {
        var cart = new Cart();
        cart.setTotalCost(new BigDecimal(1));
        when(httpSession.getAttribute(eq("com.es.phoneshop.model.cart.DefaultCartService.cart"))).thenReturn(cart);

        when(request.getParameter("firstName")).thenReturn("1");
        when(request.getParameter("lastName")).thenReturn("1");
        when(request.getParameter("phone")).thenReturn("1");
        when(request.getParameter("deliveryAddress")).thenReturn("1");
        when(request.getParameter("deliveryDate")).thenReturn("9999-12-31");
        when(request.getParameter("paymentMethod")).thenReturn("CASH");

        servlet.doPost(request, response);

        verify(response).sendRedirect(anyString());
    }
}
