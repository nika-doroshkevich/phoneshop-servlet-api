package com.es.phoneshop.model.cart;

import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.NoSuchElementException;

public class DefaultCartService implements CartService {

    private static final String CART_SESSION_ATTRIBUTE = DefaultCartService.class.getName() + ".cart";
    private static DefaultCartService instance;
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
    public synchronized Cart getCart(HttpServletRequest request) {
        Cart cart = (Cart) request.getSession().getAttribute(CART_SESSION_ATTRIBUTE);
        if (cart == null) {
            request.getSession().setAttribute(CART_SESSION_ATTRIBUTE, cart = new Cart());
        }
        return cart;
    }

    @Override
    public synchronized void add(Cart cart, Long productId, int quantity) throws OutOfStockException {
        Product product = productDao.getEntity(productId);
        if (product.getStock() < quantity) {
            throw new OutOfStockException(product, quantity, product.getStock());
        }

        var itemOptional = cart.getItems().stream()
                .filter(cartItem -> productId.equals(cartItem.getProduct().getId()))
                .findAny();

        if (itemOptional.isPresent()) {
            var item = itemOptional.get();
            int previousQuantity = item.getQuantity();
            var newQuantity = previousQuantity + quantity;
            if (product.getStock() < newQuantity) {
                throw new OutOfStockException(product, quantity, product.getStock());
            }
            item.setQuantity(previousQuantity + quantity);
        } else {
            cart.getItems().add(new CartItem(product, quantity));
        }
        recalculateCart(cart);
    }

    @Override
    public synchronized void update(Cart cart, Long productId, int quantity) throws OutOfStockException {
        Product product = productDao.getEntity(productId);
        if (product.getStock() < quantity) {
            throw new OutOfStockException(product, quantity, product.getStock());
        }

        var item = cart.getItems().stream()
                .filter(cartItem -> productId.equals(cartItem.getProduct().getId()))
                .findAny()
                .orElseThrow(() -> new NoSuchElementException("Item doesn't exist."));

        item.setQuantity(quantity);
        recalculateCart(cart);
    }

    @Override
    public synchronized void delete(Cart cart, Long productId) {
        cart.getItems().removeIf(item ->
                productId.equals(item.getProduct().getId())
        );
        recalculateCart(cart);
    }

    @Override
    public void cleanCart(Cart cart) {
        cart.getItems().clear();
        cart.setTotalQuantity(0);
        cart.setTotalCost(null);
        cart.setCurrency(null);
    }

    private void recalculateCart(Cart cart) {
        cart.setTotalQuantity(cart.getItems().stream()
                .map(CartItem::getQuantity)
                .mapToInt(q -> q)
                .sum()
        );

        BigDecimal totalCost = cart.getItems().stream()
                .map(item -> {
                    var price = item.getProduct().getPrice();
                    var quantity = item.getQuantity();
                    return price.multiply(BigDecimal.valueOf(quantity));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setTotalCost(totalCost);
        var usd = Currency.getInstance("USD");
        cart.setCurrency(usd);
    }
}
