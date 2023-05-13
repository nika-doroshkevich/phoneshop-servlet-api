package com.es.phoneshop.model.cart;

import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;

public class DefaultCartService implements CartService {

    private static DefaultCartService instance;
    private Cart cart = new Cart();
    private ProductDao productDao;

    private DefaultCartService() {
        productDao = ArrayListProductDao.getInstance();
    }

    public static synchronized DefaultCartService getInstance() {
        if (instance == null) {
            instance = new DefaultCartService();
        }
        return instance;
    }

    @Override
    public Cart getCart() {
        return cart;
    }

    @Override
    public void add(Long productId, int quantity) throws OutOfStockException {
        Product product = productDao.getProduct(productId);
        if (product.getStock() < quantity) {
            throw new OutOfStockException(product, quantity, product.getStock());
        }

        boolean itemNotExists = true;

        for (CartItem cartItem : cart.getItems()) {
            if (productId.equals(cartItem.getProduct().getId())) {
                int previousQuantity = cartItem.getQuantity();
                cartItem.setQuantity(previousQuantity + quantity);
                itemNotExists = false;
                return;
            }
        }
        if (itemNotExists) {
            cart.getItems().add(new CartItem(product, quantity));
        }
    }
}
