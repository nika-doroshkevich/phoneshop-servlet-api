package com.es.phoneshop.model.product;

import com.es.phoneshop.model.BaseDao;
import com.es.phoneshop.model.product.price.ProductPrice;
import com.es.phoneshop.model.product.price.ProductPricesDto;

import java.math.BigDecimal;
import java.util.List;

public interface ProductDao extends BaseDao<Product> {

    ProductPricesDto getProductPrices(Long id);

    List<Product> findProducts();

    List<Product> findProducts(String query, SortField sortField, SortOrder sortOrder);

    List<Product> findProducts(String description, String searchOption, BigDecimal minPrice, BigDecimal maxPrice);

    void delete(Long id);

    List<Product> findAllProducts();

    void addProductPrice(ProductPrice productPrice);
}
