package com.es.phoneshop.model.product;

import com.es.phoneshop.model.product.price.ProductPrice;
import com.es.phoneshop.model.product.price.ProductPricesDto;

import java.util.List;

public interface ProductDao {
    Product getProduct(Long id);

    ProductPricesDto getProductPrices(Long id);

    List<Product> findProducts();

    List<Product> findProducts(String query, SortField sortField, SortOrder sortOrder);

    void save(Product product);

    void delete(Long id);

    List<Product> findAllProducts();

    void addProductPrice(ProductPrice productPrice);
}
