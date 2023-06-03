package com.es.phoneshop.model.product;

import jakarta.servlet.http.HttpServletRequest;

import java.util.LinkedList;

public class RecentlyViewedProductsServiceImpl implements RecentlyViewedProductsService {

    private static final String RECENTLY_VIEWED_PRODUCTS_SESSION_ATTRIBUTE = ArrayListProductDao.class.getName() + ".recentlyViewedProducts";
    private static final int RECENTLY_VIEWED_PRODUCTS_LIST_SIZE = 3;

    private static RecentlyViewedProductsServiceImpl instance;

    public static synchronized RecentlyViewedProductsServiceImpl getInstance() {
        if (instance == null) {
            instance = new RecentlyViewedProductsServiceImpl();
        }
        return instance;
    }

    @Override
    public void addProductToRecentlyViewed(HttpServletRequest httpServletRequest, Product product) {
        var currentSession = httpServletRequest.getSession();
        LinkedList<Product> productsFromSession = (LinkedList<Product>) currentSession.getAttribute(RECENTLY_VIEWED_PRODUCTS_SESSION_ATTRIBUTE);
        LinkedList<Product> products;
        if (productsFromSession == null) {
            products = new LinkedList<>();
            products.addFirst(product);
        } else {
            products = productsFromSession;

            products.stream()
                    .filter(p -> p.getId().equals(product.getId()))
                    .findAny()
                    .ifPresent(products::remove);

            products.addFirst(product);

            if (products.size() > RECENTLY_VIEWED_PRODUCTS_LIST_SIZE) {
                products.removeLast();
            }
        }
        currentSession.setAttribute(RECENTLY_VIEWED_PRODUCTS_SESSION_ATTRIBUTE, products);
    }

    @Override
    public LinkedList<Product> getRecentlyViewedProducts(HttpServletRequest httpServletRequest) {
        var currentSession = httpServletRequest.getSession();
        LinkedList<Product> productsFromSession = (LinkedList<Product>) currentSession.getAttribute(RECENTLY_VIEWED_PRODUCTS_SESSION_ATTRIBUTE);
        if (productsFromSession == null) {
            return new LinkedList<>();
        } else {
            return productsFromSession;
        }
    }
}
