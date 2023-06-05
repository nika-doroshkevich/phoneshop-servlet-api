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
import com.es.phoneshop.utils.ParseRequestUtil;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
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

        int quantity;
        try {
            quantity = ParseRequestUtil.getQuantity(request, quantityRequest);
            cartService.add(cartService.getCart(request), productId, quantity);
        } catch (ParseException | OutOfStockException e) {
            ParseRequestUtil.handleError(errors, productId, e);
        }

        if (errors.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/products?message=Cart added successfully");
        } else {
            request.setAttribute("errors", errors);
            doGet(request, response);
        }
    }
}
