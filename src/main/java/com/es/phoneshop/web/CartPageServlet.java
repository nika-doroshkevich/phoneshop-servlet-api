package com.es.phoneshop.web;

import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.cart.DefaultCartService;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

public class CartPageServlet extends HttpServlet {

    protected static final String CART_JSP = "/WEB-INF/pages/cart.jsp";

    private CartService cartService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        cartService = DefaultCartService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("cart", cartService.getCart(request));
        request.getRequestDispatcher(CART_JSP).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        var productIds = request.getParameterValues("productId");
        var quantities = request.getParameterValues("quantity");

        Map<Long, String> errors = new HashMap<>();

        for (int i = 0; i < productIds.length; i++) {
            Long productId = Long.valueOf(productIds[i]);
            if (validate(request, response, errors, productId, quantities[i])) {
                return;
            }

            int quantity;
            try {
                quantity = getQuantity(request, quantities[i]);
                cartService.update(cartService.getCart(request), productId, quantity);
            } catch (ParseException | OutOfStockException e) {
                handleError(errors, productId, e);
            }
        }

        if (errors.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/cart?message=Cart updated successfully");
        } else {
            request.setAttribute("errors", errors);
            doGet(request, response);
        }
    }

    private boolean validate(HttpServletRequest request, HttpServletResponse response, Map<Long, String> errors,
                             Long productId, String quantityRequest) throws ServletException, IOException {
        if (!(quantityRequest.matches("^[1-9]\\d{0,2}(,\\d{3})*$")
                || quantityRequest.matches("^[1-9]\\d{0,2}(\\.\\d{3})*$")
                || quantityRequest.matches("^\\d+$"))) {

            errors.put(productId, "Quantity should be a positive integer number");
            request.setAttribute("errors", errors);
            doGet(request, response);
            return true;
        }
        return false;
    }

    private void handleError(Map<Long, String> errors, Long productId, Exception e) {
        if (e.getClass().equals(ParseException.class)) {
            errors.put(productId, "Quantity of products should be a number");
        } else if (e.getClass().equals(OutOfStockException.class)) {
            errors.put(productId, "Out of stock, available " + ((OutOfStockException) e).getStockAvailable());
        }
    }

    private int getQuantity(HttpServletRequest request, String quantityString) throws ParseException {
        var format = NumberFormat.getInstance(request.getLocale());
        return format.parse(quantityString).intValue();
    }
}
