package com.terraorm.core;

import java.util.List;

public interface CrudRepository<T, ID> {

    T save(T entity) throws Exception;

    T update(T entity) throws Exception;

    T saveOrUpdate(T entity) throws Exception;

    T findById(ID id) throws Exception;

    List<T> findAll() throws Exception;

    List<T> findWhere(String where, Object... params) throws Exception;

    List<T> findPage(int offset, int limit) throws Exception;

    void delete(T entity) throws Exception;

    void deleteById(ID id) throws Exception;
}
