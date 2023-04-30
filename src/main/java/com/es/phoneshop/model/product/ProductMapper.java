package com.es.phoneshop.model.product;

public class ProductMapper {

    public static void updateProduct(Product product, Product productToUpdate) {
        productToUpdate.setId(product.getId());
        productToUpdate.setCode(product.getCode());
        productToUpdate.setDescription(product.getDescription());
        productToUpdate.setPrice(product.getPrice());
        productToUpdate.setCurrency(product.getCurrency());
        productToUpdate.setStock(product.getStock());
        productToUpdate.setImageUrl(product.getImageUrl());
    }
}
