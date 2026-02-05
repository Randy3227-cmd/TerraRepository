package quickcrud.core;

import java.util.List;

public interface CrudRepository<T, ID> {

    T save(T entity) throws Exception;

    T findById(ID id) throws Exception;

    List<T> findAll() throws Exception;

    void delete(T entity) throws Exception;

    void deleteById(ID id) throws Exception;
}
