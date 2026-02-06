package com.terraorm.core;

import java.util.List;

public class TerraRepository<T, ID> implements CrudRepository<T, ID> {

    private final Class<T> clazz;
    private final EntityManager em = new EntityManager();

    public TerraRepository(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public T save(T entity) throws Exception {
        return em.save(entity);
    }

    @Override
    public T update(T entity) throws Exception {
        return em.update(entity);
    }

    @Override
    public T saveOrUpdate(T entity) throws Exception {
        return em.saveOrUpdate(entity);
    }

    @Override
    public T findById(ID id) throws Exception {
        return em.findById(clazz, id);
    }

    @Override
    public List<T> findAll() throws Exception {
        return em.findAll(clazz);
    }

    @Override
    public List<T> findWhere(String where, Object... params) throws Exception {
        return em.findWhere(clazz, where, params);
    }

    @Override
    public List<T> findPage(int offset, int limit) throws Exception {
        return em.findPage(clazz, offset, limit);
    }

    @Override
    public void delete(T entity) throws Exception {
        em.delete(entity);
    }

    @Override
    public void deleteById(ID id) throws Exception {
        em.deleteById(clazz, id);
    }
}
