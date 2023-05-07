package com.es.phoneshop.model.product;

import com.es.phoneshop.exception.BadRequestException;

import java.util.ArrayList;
import java.util.Comparator;
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

    private static ProductDao instance;

    private long maxId;
    private List<Product> products;
    private final ReadWriteLock rwLock;

    public static synchronized ProductDao getInstance() {
        if (instance == null) {
            instance = new ArrayListProductDao();
        }
        return instance;
    }

    private ArrayListProductDao() {
        this.rwLock = new ReentrantReadWriteLock();
        this.products = new ArrayList<>();
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
}
