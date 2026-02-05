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
    public T findById(ID id) throws Exception {
        return em.findById(clazz, id);
    }

    @Override
    public List<T> findAll() throws Exception {
        return em.findAll(clazz);
    }

    @Override
    public void delete(T entity) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public void deleteById(ID id) {
        throw new UnsupportedOperationException("TODO");
    }
}
