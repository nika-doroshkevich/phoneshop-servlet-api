package com.es.phoneshop.web;

import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.model.product.SearchOption;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdvancedSearchServlet extends HttpServlet {

    private ProductDao productDao;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        productDao = ArrayListProductDao.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        var fromSearch = request.getParameter("fromSearch");
        var description = request.getParameter("description");
        var searchOption = request.getParameter("searchOption");
        var minPrice = request.getParameter("minPrice");
        var maxPrice = request.getParameter("maxPrice");

        List<Product> products;

        if (isBlank(fromSearch)) {
            products = new ArrayList<>();
        } else {
            if (isBlank(description) && isBlank(minPrice) && isBlank(maxPrice)) {
                products = productDao.findProducts();
            } else {
                BigDecimal minP = isBlank(minPrice) ? null : new BigDecimal(minPrice);
                BigDecimal maxP = isBlank(maxPrice) ? null : new BigDecimal(maxPrice);

                products = productDao.findProducts(description, searchOption, minP, maxP);
            }


        }


        request.setAttribute("products", products);

        request.setAttribute("searchOptions", Arrays.asList(SearchOption.values()));
        request.getRequestDispatcher("/WEB-INF/pages/advancedSearch.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        var description = request.getParameter("description");
        var searchOption = request.getParameter("searchOption");
        var minPrice = request.getParameter("minPrice");
        var maxPrice = request.getParameter("maxPrice");
        System.out.println();

        if (isBlank(description) && isBlank(minPrice) && isBlank(maxPrice)) {
            var products = productDao.findProducts();

            request.setAttribute("products", products);
        }

        request.getRequestDispatcher("/WEB-INF/pages/advancedSearch.jsp").forward(request, response);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
