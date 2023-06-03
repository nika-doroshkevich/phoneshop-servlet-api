package com.es.phoneshop.web;

import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.cart.DefaultCartService;
import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.model.product.RecentlyViewedProductsService;
import com.es.phoneshop.model.product.RecentlyViewedProductsServiceImpl;
import com.es.phoneshop.model.product.SortField;
import com.es.phoneshop.model.product.SortOrder;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ProductListPageServlet extends HttpServlet {

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
        String query = request.getParameter("query");
        String sortField = request.getParameter("sort");
        String sortOrder = request.getParameter("order");

        var products = productDao.findProducts(query,
                Optional.ofNullable(sortField).map(SortField::valueOf).orElse(null),
                Optional.ofNullable(sortOrder).map(SortOrder::valueOf).orElse(null)
        );

        request.setAttribute("products", products);

        var list = recentlyViewedProductsService.getRecentlyViewedProducts(request);
        List<Product> recentlyViewedProducts = new ArrayList<>(list);
        request.setAttribute("recentlyViewedProducts", recentlyViewedProducts);
        request.getRequestDispatcher("/WEB-INF/pages/productList.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        var productIdRequest = request.getParameter("productId");
        var quantityRequest = request.getParameter("quantity");

        Map<Long, String> errors = new HashMap<>();
        Long productId = Long.valueOf(productIdRequest);
        if (validate(request, response, errors, productId, quantityRequest)) {
            return;
        }

        int quantity;
        try {
            quantity = getQuantity(request, quantityRequest);
            cartService.add(cartService.getCart(request), productId, quantity);
        } catch (ParseException | OutOfStockException e) {
            handleError(errors, productId, e);
        }

        if (errors.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/products?message=Cart added successfully");
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
