package com.es.phoneshop.web;

import com.es.phoneshop.exception.BadRequestException;
import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.ProductDao;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ProductDetailsPageServlet extends HttpServlet {

    private static final String PRICES = "prices";

    private ProductDao productDao;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        productDao = ArrayListProductDao.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        var path = request.getPathInfo();
        validatePath(path);
        var productId = retrieveProductId(path);

        String requestPage;
        Map<String, Object> attributes = new HashMap<>();

        if (path.contains(PRICES)) {
            var prices = productDao.getProductPrices(productId);
            attributes.put("productPricesDto", prices);
            requestPage = "productPrices.jsp";
        } else {
            var product = productDao.getProduct(productId);
            attributes.put("product", product);
            requestPage = "product.jsp";
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

        if (path == null || path.trim().isEmpty()) {
            wrongPath = true;
        }

        // TODO add validation: path matches pattern '/{number}' or "/prices/{number}"

        if (wrongPath) {
            throw new BadRequestException("Incorrect request path for servlet with name 'product'");
        }
    }
}
