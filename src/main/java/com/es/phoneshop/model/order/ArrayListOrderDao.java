package com.es.phoneshop.model.order;

import com.es.phoneshop.exception.BadRequestException;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ArrayListOrderDao implements OrderDao {

    private static OrderDao instance;

    private long maxId;
    private List<Order> orders;
    private final ReadWriteLock rwLock;

    private ArrayListOrderDao() {
        this.rwLock = new ReentrantReadWriteLock();
        this.orders = new ArrayList<>();
    }

    public static synchronized OrderDao getInstance() {
        if (instance == null) {
            instance = new ArrayListOrderDao();
        }
        return instance;
    }

    @Override
    public Order getEntity(Long id) throws NoSuchElementException {
        if (id == null) {
            throw new BadRequestException("Order id can not be null.");
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
    public Order getOrderBySecureId(String secureId) throws NoSuchElementException {
        if (secureId == null) {
            throw new BadRequestException("Order id can not be null.");
        }

        Lock readLock = rwLock.readLock();
        readLock.lock();

        try {
            return getBySecureId(secureId);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public void save(Order order) throws NoSuchElementException {
        Lock writeLock = rwLock.writeLock();
        writeLock.lock();

        try {
            var orderId = order.getId();
            if (orderId != null) {
                orders.remove(getEntity(orderId));
                orders.add(order);

            } else {
                order.setId(maxId++);
                orders.add(order);
            }
        } finally {
            writeLock.unlock();
        }
    }

    private Order getById(Long id) {
        return orders.stream()
                .filter(p -> id.equals(p.getId()))
                .findAny()
                .orElseThrow(() -> new NoSuchElementException("Order with id " + id + " not found."));
    }

    private Order getBySecureId(String secureId) {
        return orders.stream()
                .filter(p -> secureId.equals(p.getSecureId()))
                .findAny()
                .orElseThrow(() -> new NoSuchElementException("Order not found."));
    }
}
