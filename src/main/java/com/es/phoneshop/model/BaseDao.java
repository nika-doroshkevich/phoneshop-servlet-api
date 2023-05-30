package com.es.phoneshop.model;

public interface BaseDao<T> {

    T getEntity(Long id);

    void save(T entity);
}
