package com.es.phoneshop.web;

import com.es.phoneshop.exception.BadRequestException;
import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.cart.DefaultCartService;
import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.model.product.RecentlyViewedProductsService;
import com.es.phoneshop.model.product.RecentlyViewedProductsServiceImpl;
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

import static org.apache.commons.lang3.StringUtils.isBlank;

public class ProductDetailsPageServlet extends HttpServlet {

    private static final String PRICES = "prices";

    private ProductDao productDao;
    private CartService cartService;
    private RecentlyViewedProductsService recentlyViewedProductsService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        productDao = ArrayListProductDao.getInstance();
        cartService = DefaultCartService.getInstance();
        recentlyViewedProductsService = RecentlyViewedProductsServiceImpl.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        var path = request.getPathInfo();
        validatePath(path);
        var productId = retrieveProductId(path);

        String error = (String) request.getAttribute("error");
        String message = request.getParameter("message");
        if (message != null && (error == null || error.isEmpty())) {
            request.setAttribute("message", message);
        }

        String requestPage;
        Map<String, Object> attributes = new HashMap<>();

        if (path.contains(PRICES)) {
            var prices = productDao.getProductPrices(productId);
            attributes.put("productPricesDto", prices);
            requestPage = "productPrices.jsp";
        } else {
            var product = productDao.getEntity(productId);
            attributes.put("product", product);
            requestPage = "product.jsp";
            Cart cartFromSession = cartService.getCart(request);
            request.setAttribute("cart", cartFromSession);
            recentlyViewedProductsService.addProductToRecentlyViewed(request, product);
        }
        dispatchRequest(request, response, requestPage, attributes);
    }

    private Long retrieveProductId(String path) {
        var paths = path.split("/");
        var lastElement = paths[paths.length - 1];
        return Long.valueOf(lastElement);
    }

    private void dispatchRequest(HttpServletRequest request, HttpServletResponse response,
                                 String requestPage, Map<String, Object> attributes) throws ServletException, IOException {
        attributes.forEach(request::setAttribute);
        request.getRequestDispatcher("/WEB-INF/pages/" + requestPage).forward(request, response);
    }

    private void validatePath(String path) {
        boolean wrongPath = false;

        if (isBlank(path)) {
            wrongPath = true;
        }

        // TODO add validation: path matches pattern '/{number}' or "/prices/{number}"

        if (wrongPath) {
            throw new BadRequestException("Incorrect request path for servlet with name 'product'");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (validate(request, response)) {
            return;
        }

        var productIdFromPath = request.getPathInfo().substring(1);
        var productId = Long.valueOf(productIdFromPath);
        var locale = request.getLocale();
        var quantityString = request.getParameter("quantity");
        int quantity;
        var cart = cartService.getCart(request);
        String errorMessage = null;

        try {
            var format = NumberFormat.getInstance(locale);
            quantity = format.parse(quantityString).intValue();
            cartService.add(cart, productId, quantity);
        } catch (ParseException ex) {
            errorMessage = "Quantity of products should be a number";
        } catch (OutOfStockException e) {
            errorMessage = "Out of stock, available " + e.getStockAvailable();
        }
        if (validate(request, response, errorMessage)) {
            return;
        }

        response.sendRedirect(request.getContextPath()
                + "/products/" + productId + "?message=Product added to cart");
    }

    private boolean validate(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        var quantityString = request.getParameter("quantity");
        if (!(quantityString.matches("^[1-9]\\d{0,2}(,\\d{3})*$")
                || quantityString.matches("^[1-9]\\d{0,2}(\\.\\d{3})*$")
                || quantityString.matches("^\\d+$"))) {

            request.setAttribute("error", "Quantity should be a positive integer number");
            doGet(request, response);
            return true;
        }
        return false;
    }

    private boolean validate(HttpServletRequest request, HttpServletResponse response, String errorMessage) throws ServletException, IOException {
        if (errorMessage != null) {
            request.setAttribute("error", errorMessage);
            doGet(request, response);
            return true;
        }
        return false;
    }
}
