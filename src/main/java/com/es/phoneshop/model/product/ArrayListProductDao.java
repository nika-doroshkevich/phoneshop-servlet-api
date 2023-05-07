package com.es.phoneshop.model.product;

import com.es.phoneshop.exception.BadRequestException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Currency;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.es.phoneshop.model.product.SortField.description;
import static com.es.phoneshop.model.product.SortField.price;
import static com.es.phoneshop.model.product.SortOrder.desc;

public class ArrayListProductDao implements ProductDao {

    private long maxId;
    private List<Product> products;
    private final ReadWriteLock rwLock;

    public ArrayListProductDao() {
        this.rwLock = new ReentrantReadWriteLock();
        this.products = new ArrayList<>();
        saveSampleProducts();
    }

    @Override
    public Product getProduct(Long id) throws NoSuchElementException {
        if (id == null) {
            throw new BadRequestException("Product id can not be null.");
        }

        Lock readLock = rwLock.readLock();
        readLock.lock();

        try {
            return getById(id);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public List<Product> findProducts() {
        return products.stream()
                .filter(Product::isAvailableForSale)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findProducts(String query, SortField sortField, SortOrder sortOrder) {
        Lock readLock = rwLock.readLock();
        readLock.lock();

        try {
            var comparedByField = compareByField(sortField);
            var comparedByFieldAndOrder = compareByOrder(comparedByField, sortOrder);

            if (query == null || query.trim().isEmpty()) {
                return products.stream()
                        .filter(Product::isAvailableForSale)
                        .sorted(comparedByFieldAndOrder)
                        .collect(Collectors.toList());
            }

            String queryToLower = query.toLowerCase();
            String[] queryWords = queryToLower.split("\\s+");

            return products.stream()
                    .map(ProductDto::new)
                    .peek(productDto -> countMatches(productDto, queryWords, queryToLower))
                    .filter(productDto -> productDto.getNumberOfMatches() > 0)
                    .sorted(Comparator.comparing(ProductDto::getNumberOfMatches).reversed())
                    .map(ProductDto::getProduct)
                    .sorted(comparedByFieldAndOrder)
                    .filter(Product::isAvailableForSale)
                    .collect(Collectors.toList());

            //For second implementation
//            return products.stream()
//                    .filter(combineOr(queryWords))
//                    .collect(Collectors.toList());

        } finally {
            readLock.unlock();
        }
    }

    @Override
    public void save(Product product) throws NoSuchElementException {
        Lock writeLock = rwLock.writeLock();
        writeLock.lock();

        try {
            var productId = product.getId();
            if (productId != null) {
                var foundProduct = getById(productId);
                ProductMapper.updateProduct(product, foundProduct);
            } else {
                product.setId(maxId++);
                products.add(product);
            }
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void delete(Long id) throws NoSuchElementException {
        Lock writeLock = rwLock.writeLock();
        writeLock.lock();

        try {
            var foundProduct = getById(id);
            products.remove(foundProduct);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public List<Product> findAllProducts() {
        Lock readLock = rwLock.readLock();
        readLock.lock();

        try {
            return products;
        } finally {
            readLock.unlock();
        }
    }

    private Product getById(Long id) {
        return products.stream()
                .filter(p -> id.equals(p.getId()))
                .findAny()
                .orElseThrow(() -> new NoSuchElementException("Product not found with id: " + id));
    }

    private void countMatches(ProductDto productDto, String[] queryWords, String query) {
        String description = productDto.getProduct().getDescription().toLowerCase();
        int numberOfMatches = 0;
        if (query.equals(description)) {
            numberOfMatches = Integer.MAX_VALUE;
        } else {
            for (String s : queryWords) {
                if (description.contains(s)) {
                    numberOfMatches++;
                }
            }
        }
        productDto.setNumberOfMatches(numberOfMatches);
    }

    private Comparator<Product> compareByField(SortField sortField) {
        Comparator<Product> comparator;
        if (sortField == description) {
            comparator = Comparator.comparing(Product::getDescription);
        } else if (sortField == price) {
            comparator = Comparator.comparing(Product::getPrice);
        } else {
            //Default sorting
            comparator = Comparator.comparing(Product::getDescription);
        }
        return comparator;
    }

    private Comparator<Product> compareByOrder(Comparator<Product> comparator, SortOrder sortOrder) {
        if (sortOrder == desc) {
            return comparator.reversed();
        }
        return comparator;
    }

    //Implementation using only streams (without creating additional classes), but sorting does not work
    //filter and combineOr methods
    private Predicate<Product> filter(String searchParameter) {
        return p -> p.getDescription().contains(searchParameter);
    }

    private Predicate<Product> combineOr(String[] queryWords) {
        var predicate = filter(queryWords[0]);

        if (queryWords.length == 1) {
            return predicate;
        }

        for (int i = 1; i < queryWords.length; i++) {
            predicate = predicate.or(filter(queryWords[i]));
        }
        return predicate;
    }

    private void saveSampleProducts() {
        Currency usd = Currency.getInstance("USD");
        save(new Product("sgs", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg"));
        save(new Product("sgs2", "Samsung Galaxy S II", new BigDecimal(200), usd, 0, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20II.jpg"));
        save(new Product("sgs3", "Samsung Galaxy S III", new BigDecimal(300), usd, 5, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20III.jpg"));
        save(new Product("iphone", "Apple iPhone", new BigDecimal(200), usd, 10, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Apple/Apple%20iPhone.jpg"));
        save(new Product("iphone6", "Apple iPhone 6", new BigDecimal(1000), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Apple/Apple%20iPhone%206.jpg"));
        save(new Product("htces4g", "HTC EVO Shift 4G", new BigDecimal(320), usd, 3, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/HTC/HTC%20EVO%20Shift%204G.jpg"));
        save(new Product("sec901", "Sony Ericsson C901", new BigDecimal(420), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Sony/Sony%20Ericsson%20C901.jpg"));
        save(new Product("xperiaxz", "Sony Xperia XZ", new BigDecimal(120), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Sony/Sony%20Xperia%20XZ.jpg"));
        save(new Product("nokia3310", "Nokia 3310", new BigDecimal(70), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Nokia/Nokia%203310.jpg"));
        save(new Product("palmp", "Palm Pixi", new BigDecimal(170), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Palm/Palm%20Pixi.jpg"));
        save(new Product("simc56", "Siemens C56", new BigDecimal(70), usd, 20, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20C56.jpg"));
        save(new Product("simc61", "Siemens C61", new BigDecimal(80), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20C61.jpg"));
        save(new Product("simsxg75", "Siemens SXG75", new BigDecimal(150), usd, 40, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20SXG75.jpg"));
    }
}
