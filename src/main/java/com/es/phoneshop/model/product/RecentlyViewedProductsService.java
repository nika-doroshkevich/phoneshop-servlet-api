package com.es.phoneshop.model.product;

import jakarta.servlet.http.HttpServletRequest;

import java.util.LinkedList;

public interface RecentlyViewedProductsService {

    void addProductToRecentlyViewed(HttpServletRequest httpServletRequest, Product product);

    LinkedList<Product> getRecentlyViewedProducts(HttpServletRequest httpServletRequest);
}
