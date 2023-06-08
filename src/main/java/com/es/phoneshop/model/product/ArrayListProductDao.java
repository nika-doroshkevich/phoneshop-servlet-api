package com.es.phoneshop.model.product;

import com.es.phoneshop.exception.BadRequestException;
import com.es.phoneshop.model.product.price.ProductPrice;
import com.es.phoneshop.model.product.price.ProductPricesDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import static com.es.phoneshop.model.product.SearchOption.ALL_WORDS;
import static com.es.phoneshop.model.product.SortField.description;
import static com.es.phoneshop.model.product.SortField.price;
import static com.es.phoneshop.model.product.SortOrder.desc;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class ArrayListProductDao implements ProductDao {

    private static ProductDao instance;

    private long maxId;
    private List<Product> products;
    private List<ProductPrice> priceHistory;
    private final ReadWriteLock rwLock;

    private ArrayListProductDao() {
        this.rwLock = new ReentrantReadWriteLock();
        this.products = new ArrayList<>();
        this.priceHistory = new ArrayList<>();
    }

    public static synchronized ProductDao getInstance() {
        if (instance == null) {
            instance = new ArrayListProductDao();
        }
        return instance;
    }

    @Override
    public Product getEntity(Long id) throws NoSuchElementException {
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
    public ProductPricesDto getProductPrices(Long id) {
        var productDescription = getById(id).getDescription();
        var prices = priceHistory.stream()
                .filter(productPrice -> id.equals(productPrice.getProductId()))
                .sorted(Comparator.comparing(ProductPrice::getDate).reversed())
                .collect(Collectors.toList());

        return ProductPricesDto.builder()
                .productDescription(productDescription)
                .prices(prices)
                .build();
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

            if (isBlank(query)) {
                return products.stream()
                        .filter(Product::isAvailableForSale)
                        .sorted(comparedByFieldAndOrder)
                        .collect(Collectors.toList());
            }

            String queryToLower = query.toLowerCase();
            String[] queryWords = queryToLower.split("\\s+");

            if (sortField == null || sortOrder == null) {
                return products.stream()
                        .map(ProductDto::new)
                        .peek(productDto -> countMatches(productDto, queryWords, queryToLower))
                        .filter(productDto -> productDto.getNumberOfMatches() > 0)
                        .sorted(Comparator.comparing(ProductDto::getNumberOfMatches).reversed())
                        .map(ProductDto::getProduct)
                        .filter(Product::isAvailableForSale)
                        .collect(Collectors.toList());
            }

            return products.stream()
                    .map(ProductDto::new)
                    .peek(productDto -> countMatches(productDto, queryWords, queryToLower))
                    .filter(productDto -> productDto.getNumberOfMatches() > 0)
                    .sorted(Comparator.comparing(ProductDto::getNumberOfMatches).reversed())
                    .map(ProductDto::getProduct)
                    .sorted(comparedByFieldAndOrder)
                    .filter(Product::isAvailableForSale)
                    .collect(Collectors.toList());
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public List<Product> findProducts(String description, String searchOption, BigDecimal minPrice, BigDecimal maxPrice) {
        List<Product> productsList = new ArrayList<>();

        if (description != null && !description.trim().isEmpty()) {
            if (searchOption.equals(ALL_WORDS.name())) {
                productsList = products.stream()
                        .filter(p -> p.getDescription().equals(description))
                        .collect(Collectors.toList());
            } else {
                String queryToLower = description.toLowerCase();
                String[] queryWords = queryToLower.split("\\s+");

                productsList = products.stream()
                        .map(ProductDto::new)
                        .peek(productDto -> countMatches(productDto, queryWords, queryToLower))
                        .filter(productDto -> productDto.getNumberOfMatches() > 0)
                        .sorted(Comparator.comparing(ProductDto::getNumberOfMatches).reversed())
                        .map(ProductDto::getProduct)
                        .filter(Product::isAvailableForSale)
                        .collect(Collectors.toList());
            }
        } else {
            productsList = products;
        }

        if (minPrice != null) {
            productsList = productsList.stream()
                    .filter(p -> p.getPrice().compareTo(minPrice) >= 0)
                    .collect(Collectors.toList());
        }

        if (maxPrice != null) {
            productsList = productsList.stream()
                    .filter(p -> p.getPrice().compareTo(maxPrice) <= 0)
                    .collect(Collectors.toList());
        }


        return productsList;
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
                var productPrice = new ProductPrice(maxId - 1, product.getPrice(), LocalDate.now(), product.getCurrency());
                addProductPrice(productPrice);
            }
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void addProductPrice(ProductPrice productPrice) {
        this.priceHistory.add(productPrice);
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
                .orElseThrow(() -> new NoSuchElementException("Product with id " + id + " not found."));
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
}
