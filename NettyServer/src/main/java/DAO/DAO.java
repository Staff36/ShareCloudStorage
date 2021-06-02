package DAO;


import Entities.User;

import java.sql.SQLException;

public interface DAO<T> {
    T getInstanceByName(T entity);
    void create(T entity);
    int update(T oldEntity, T newEntity);
    int delete(T entity);


}
